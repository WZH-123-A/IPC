package com.ccs.ipc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ccs.ipc.dto.patientdto.ConsultationMessageListRequest;
import com.ccs.ipc.dto.patientdto.ConsultationMessageListResponse;
import com.ccs.ipc.dto.patientdto.ConsultationMessageResponse;
import com.ccs.ipc.dto.patientdto.SendMessageRequest;
import com.ccs.ipc.entity.ConsultationMessage;
import com.ccs.ipc.entity.ConsultationSession;
import com.ccs.ipc.ai.QwenAiProperties;
import com.ccs.ipc.ai.QwenAiService;
import com.ccs.ipc.mapper.ConsultationMessageMapper;
import com.ccs.ipc.service.IConsultationMessageService;
import com.ccs.ipc.service.IConsultationSessionService;
import com.ccs.ipc.websocket.WebSocketService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * <p>
 * 问诊消息记录表 服务实现类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
@Slf4j
@Service
public class ConsultationMessageServiceImpl extends ServiceImpl<ConsultationMessageMapper, ConsultationMessage> implements IConsultationMessageService {

    @Autowired
    private IConsultationSessionService consultationSessionService;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private QwenAiService qwenAiService;

    @Autowired
    private QwenAiProperties qwenAiProperties;

    @Override
    public ConsultationMessageListResponse getSessionMessages(Long sessionId, Long userId, ConsultationMessageListRequest request) {
        // 验证会话归属
        ConsultationSession session = consultationSessionService.getById(sessionId);
        if (session == null || session.getIsDeleted() == 1) {
            throw new RuntimeException("问诊会话不存在");
        }
        if (!session.getPatientId().equals(userId)) {
            throw new RuntimeException("无权访问此问诊会话");
        }

        Page<ConsultationMessage> page = new Page<>(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<ConsultationMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ConsultationMessage::getSessionId, sessionId)
                .eq(ConsultationMessage::getIsDeleted, 0)
                .orderByAsc(ConsultationMessage::getCreateTime);

        Page<ConsultationMessage> result = this.page(page, queryWrapper);

        ConsultationMessageListResponse response = new ConsultationMessageListResponse();
        response.setTotal(result.getTotal());
        response.setCurrent(result.getCurrent());
        response.setSize(result.getSize());

        List<ConsultationMessageResponse> responseList = result.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        response.setRecords(responseList);

        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConsultationMessageResponse sendMessage(Long userId, SendMessageRequest request) {
        // 验证会话归属
        ConsultationSession session = consultationSessionService.getById(request.getSessionId());
        if (session == null || session.getIsDeleted() == 1) {
            throw new RuntimeException("问诊会话不存在");
        }
        if (!session.getPatientId().equals(userId)) {
            throw new RuntimeException("无权操作此问诊会话");
        }
        if (session.getStatus() != 0) {
            throw new RuntimeException("问诊会话已结束或已取消");
        }

        ConsultationMessage message = new ConsultationMessage();
        message.setSessionId(request.getSessionId());
        message.setSenderId(userId);
        message.setSenderType((byte) 1); // 患者
        message.setMessageType(request.getMessageType());
        message.setContent(request.getContent());
        message.setIsRead((byte) 0);
        message.setIsDeleted((byte) 0);

        this.save(message);

        ConsultationMessageResponse messageResponse = convertToResponse(message);

        // 通过WebSocket推送消息到会话的所有参与者
        webSocketService.sendMessageToSession(request.getSessionId(), messageResponse);

        // AI问诊：自动生成AI回复并推送（失败不影响用户消息发送）
        if (session.getSessionType() != null && session.getSessionType() == 1) {
            try {
                int maxHistory = qwenAiProperties.getMaxHistoryMessages() != null ? qwenAiProperties.getMaxHistoryMessages() : 20;
                List<ConsultationMessage> history = getRecentMessagesForAi(request.getSessionId(), maxHistory);
                String reply = qwenAiService.generateReply(session.getTitle(), history);

                ConsultationMessage aiMessage = new ConsultationMessage();
                aiMessage.setSessionId(request.getSessionId());
                aiMessage.setSenderId(0L);
                aiMessage.setSenderType((byte) 3); // AI
                aiMessage.setMessageType((byte) 1); // 文本
                aiMessage.setContent(reply);
                aiMessage.setAiModel(qwenAiProperties.getModel());
                aiMessage.setIsRead((byte) 0);
                aiMessage.setIsDeleted((byte) 0);
                this.save(aiMessage);

                ConsultationMessageResponse aiResponse = convertToResponse(aiMessage);
                webSocketService.sendMessageToSession(request.getSessionId(), aiResponse);
            } catch (Exception e) {
                log.warn("AI问诊回复生成失败(sessionId={}): {}", request.getSessionId(), e.getMessage());
                try {
                    ConsultationMessage aiMessage = new ConsultationMessage();
                    aiMessage.setSessionId(request.getSessionId());
                    aiMessage.setSenderId(0L);
                    aiMessage.setSenderType((byte) 3); // AI
                    aiMessage.setMessageType((byte) 1); // 文本
                    aiMessage.setContent("AI暂时不可用，请稍后再试。");
                    aiMessage.setAiModel(qwenAiProperties.getModel());
                    aiMessage.setIsRead((byte) 0);
                    aiMessage.setIsDeleted((byte) 0);
                    this.save(aiMessage);

                    ConsultationMessageResponse aiResponse = convertToResponse(aiMessage);
                    webSocketService.sendMessageToSession(request.getSessionId(), aiResponse);
                } catch (Exception ignored) {
                    // 兜底：不影响主流程
                }
            }
        }

        return messageResponse;
    }

    /**
     * AI上下文：取最近N条消息（按时间正序）
     */
    private List<ConsultationMessage> getRecentMessagesForAi(Long sessionId, int maxCount) {
        if (sessionId == null || maxCount <= 0) return Collections.emptyList();
        LambdaQueryWrapper<ConsultationMessage> qw = new LambdaQueryWrapper<>();
        qw.eq(ConsultationMessage::getSessionId, sessionId)
                .eq(ConsultationMessage::getIsDeleted, 0)
                .orderByDesc(ConsultationMessage::getCreateTime)
                .last("LIMIT " + maxCount);
        List<ConsultationMessage> list = this.list(qw);
        if (list == null || list.isEmpty()) return Collections.emptyList();
        Collections.reverse(list);
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long messageId) {
        ConsultationMessage message = this.getById(messageId);
        if (message != null && message.getIsDeleted() == 0) {
            message.setIsRead((byte) 1);
            this.updateById(message);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllAsRead(Long sessionId, Long userId) {
        // 验证会话归属
        ConsultationSession session = consultationSessionService.getById(sessionId);
        if (session == null || session.getIsDeleted() == 1) {
            throw new RuntimeException("问诊会话不存在");
        }
        if (!session.getPatientId().equals(userId)) {
            throw new RuntimeException("无权操作此问诊会话");
        }

        // 批量更新该会话中所有未读消息为已读
        LambdaQueryWrapper<ConsultationMessage> updateWrapper = new LambdaQueryWrapper<>();
        updateWrapper.eq(ConsultationMessage::getSessionId, sessionId)
                .eq(ConsultationMessage::getIsRead, 0)
                .eq(ConsultationMessage::getIsDeleted, 0);

        ConsultationMessage updateMessage = new ConsultationMessage();
        updateMessage.setIsRead((byte) 1);
        this.update(updateMessage, updateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllAsReadByDoctor(Long sessionId, Long doctorId) {
        // 验证会话归属（医生问诊）
        ConsultationSession session = consultationSessionService.getById(sessionId);
        if (session == null || session.getIsDeleted() == 1) {
            throw new RuntimeException("问诊会话不存在");
        }
        if (session.getSessionType() != 2) {
            throw new RuntimeException("此会话不是医生问诊");
        }
        if (session.getDoctorId() == null || !session.getDoctorId().equals(doctorId)) {
            throw new RuntimeException("无权操作此问诊会话");
        }

        // 批量更新该会话中所有未读消息为已读
        LambdaQueryWrapper<ConsultationMessage> updateWrapper = new LambdaQueryWrapper<>();
        updateWrapper.eq(ConsultationMessage::getSessionId, sessionId)
                .eq(ConsultationMessage::getIsRead, 0)
                .eq(ConsultationMessage::getIsDeleted, 0);

        ConsultationMessage updateMessage = new ConsultationMessage();
        updateMessage.setIsRead((byte) 1);
        this.update(updateMessage, updateWrapper);
    }

    @Override
    public ConsultationMessageListResponse getDoctorSessionMessages(Long sessionId, Long doctorId, ConsultationMessageListRequest request) {
        // 验证会话归属（医生问诊）
        ConsultationSession session = consultationSessionService.getById(sessionId);
        if (session == null || session.getIsDeleted() == 1) {
            throw new RuntimeException("问诊会话不存在");
        }
        if (session.getSessionType() != 2) {
            throw new RuntimeException("此会话不是医生问诊");
        }
        if (session.getDoctorId() == null || !session.getDoctorId().equals(doctorId)) {
            throw new RuntimeException("无权访问此问诊会话");
        }

        Page<ConsultationMessage> page = new Page<>(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<ConsultationMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ConsultationMessage::getSessionId, sessionId)
                .eq(ConsultationMessage::getIsDeleted, 0)
                .orderByAsc(ConsultationMessage::getCreateTime);

        Page<ConsultationMessage> result = this.page(page, queryWrapper);

        ConsultationMessageListResponse response = new ConsultationMessageListResponse();
        response.setTotal(result.getTotal());
        response.setCurrent(result.getCurrent());
        response.setSize(result.getSize());

        List<ConsultationMessageResponse> responseList = result.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        response.setRecords(responseList);

        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConsultationMessageResponse sendDoctorMessage(Long doctorId, SendMessageRequest request) {
        // 验证会话归属（医生问诊）
        ConsultationSession session = consultationSessionService.getById(request.getSessionId());
        if (session == null || session.getIsDeleted() == 1) {
            throw new RuntimeException("问诊会话不存在");
        }
        if (session.getSessionType() != 2) {
            throw new RuntimeException("此会话不是医生问诊");
        }
        if (session.getDoctorId() == null || !session.getDoctorId().equals(doctorId)) {
            throw new RuntimeException("无权操作此问诊会话");
        }
        if (session.getStatus() != 0) {
            throw new RuntimeException("问诊会话已结束或已取消");
        }

        ConsultationMessage message = new ConsultationMessage();
        message.setSessionId(request.getSessionId());
        message.setSenderId(doctorId);
        message.setSenderType((byte) 2); // 医生
        message.setMessageType(request.getMessageType());
        message.setContent(request.getContent());
        message.setIsRead((byte) 0);
        message.setIsDeleted((byte) 0);

        this.save(message);

        ConsultationMessageResponse messageResponse = convertToResponse(message);

        // 通过WebSocket推送消息到会话的所有参与者
        webSocketService.sendMessageToSession(request.getSessionId(), messageResponse);

        return messageResponse;
    }

    @Override
    public ConsultationMessageListResponse getAdminSessionMessages(Long sessionId, ConsultationMessageListRequest request) {
        ConsultationSession session = consultationSessionService.getById(sessionId);
        if (session == null || session.getIsDeleted() == 1) {
            throw new RuntimeException("问诊会话不存在");
        }

        Page<ConsultationMessage> page = new Page<>(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<ConsultationMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ConsultationMessage::getSessionId, sessionId)
                .eq(ConsultationMessage::getIsDeleted, 0)
                .orderByAsc(ConsultationMessage::getCreateTime);

        Page<ConsultationMessage> result = this.page(page, queryWrapper);

        ConsultationMessageListResponse response = new ConsultationMessageListResponse();
        response.setTotal(result.getTotal());
        response.setCurrent(result.getCurrent());
        response.setSize(result.getSize());
        response.setRecords(result.getRecords().stream().map(this::convertToResponse).collect(Collectors.toList()));
        return response;
    }

    private ConsultationMessageResponse convertToResponse(ConsultationMessage message) {
        ConsultationMessageResponse response = new ConsultationMessageResponse();
        BeanUtils.copyProperties(message, response);
        return response;
    }
}
