package com.ccs.ipc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ccs.ipc.dto.patientdto.ConsultationMessageListRequest;
import com.ccs.ipc.dto.patientdto.ConsultationMessageListResponse;
import com.ccs.ipc.dto.patientdto.ConsultationMessageResponse;
import com.ccs.ipc.dto.patientdto.SendMessageRequest;
import com.ccs.ipc.entity.ConsultationMessage;
import com.ccs.ipc.entity.ConsultationSession;
import com.ccs.ipc.mapper.ConsultationMessageMapper;
import com.ccs.ipc.service.IConsultationMessageService;
import com.ccs.ipc.service.IConsultationSessionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 问诊消息记录表 服务实现类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
@Service
public class ConsultationMessageServiceImpl extends ServiceImpl<ConsultationMessageMapper, ConsultationMessage> implements IConsultationMessageService {

    @Autowired
    private IConsultationSessionService consultationSessionService;

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

        // TODO: 如果是AI问诊，调用AI接口生成回复

        return convertToResponse(message);
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

    private ConsultationMessageResponse convertToResponse(ConsultationMessage message) {
        ConsultationMessageResponse response = new ConsultationMessageResponse();
        BeanUtils.copyProperties(message, response);
        return response;
    }
}
