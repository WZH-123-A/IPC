package com.ccs.ipc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ccs.ipc.dto.patientdto.ConsultationSessionListRequest;
import com.ccs.ipc.dto.patientdto.ConsultationSessionListResponse;
import com.ccs.ipc.dto.patientdto.ConsultationSessionResponse;
import com.ccs.ipc.dto.patientdto.CreateConsultationSessionRequest;
import com.ccs.ipc.entity.ConsultationSession;
import com.ccs.ipc.mapper.ConsultationSessionMapper;
import com.ccs.ipc.service.IConsultationSessionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <p>
 * 在线问诊会话表 服务实现类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
@Service
public class ConsultationSessionServiceImpl extends ServiceImpl<ConsultationSessionMapper, ConsultationSession> implements IConsultationSessionService {

    @Override
    public ConsultationSessionListResponse getPatientSessions(Long userId, ConsultationSessionListRequest request) {
        Page<ConsultationSession> page = new Page<>(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<ConsultationSession> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ConsultationSession::getPatientId, userId)
                .eq(ConsultationSession::getIsDeleted, 0);

        if (request.getStatus() != null) {
            queryWrapper.eq(ConsultationSession::getStatus, request.getStatus());
        }

        queryWrapper.orderByDesc(ConsultationSession::getCreateTime);
        Page<ConsultationSession> result = this.page(page, queryWrapper);

        ConsultationSessionListResponse response = new ConsultationSessionListResponse();
        response.setTotal(result.getTotal());
        response.setCurrent(result.getCurrent());
        response.setSize(result.getSize());

        List<ConsultationSessionResponse> responseList = result.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        response.setRecords(responseList);

        return response;
    }

    @Override
    public ConsultationSessionResponse getSessionDetail(Long sessionId, Long userId) {
        ConsultationSession session = this.getById(sessionId);
        if (session == null || session.getIsDeleted() == 1) {
            throw new RuntimeException("问诊会话不存在");
        }

        if (!session.getPatientId().equals(userId)) {
            throw new RuntimeException("无权访问此问诊会话");
        }

        return convertToResponse(session);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConsultationSessionResponse createSession(Long userId, CreateConsultationSessionRequest request) {
        ConsultationSession session = new ConsultationSession();
        session.setSessionNo("CS" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8));
        session.setPatientId(userId);
        session.setSessionType(request.getSessionType());
        session.setTitle(request.getTitle() != null ? request.getTitle() : "未命名问诊");
        session.setStatus((byte) 0); // 进行中
        session.setStartTime(LocalDateTime.now());
        session.setIsDeleted((byte) 0);

        if (request.getSessionType() == 2 && request.getDoctorId() != null) {
            session.setDoctorId(request.getDoctorId());
        }

        this.save(session);
        return convertToResponse(session);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void endSession(Long sessionId, Long userId) {
        ConsultationSession session = this.getById(sessionId);
        if (session == null || session.getIsDeleted() == 1) {
            throw new RuntimeException("问诊会话不存在");
        }

        if (!session.getPatientId().equals(userId)) {
            throw new RuntimeException("无权操作此问诊会话");
        }

        session.setStatus((byte) 1); // 已结束
        session.setEndTime(LocalDateTime.now());
        this.updateById(session);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void endSessionByDoctor(Long sessionId, Long doctorId) {
        ConsultationSession session = this.getById(sessionId);
        if (session == null || session.getIsDeleted() == 1) {
            throw new RuntimeException("问诊会话不存在");
        }

        if (session.getSessionType() != 2) {
            throw new RuntimeException("此会话不是医生问诊");
        }

        if (session.getDoctorId() == null || !session.getDoctorId().equals(doctorId)) {
            throw new RuntimeException("无权操作此问诊会话");
        }

        session.setStatus((byte) 1); // 已结束
        session.setEndTime(LocalDateTime.now());
        this.updateById(session);
    }

    private ConsultationSessionResponse convertToResponse(ConsultationSession session) {
        ConsultationSessionResponse response = new ConsultationSessionResponse();
        BeanUtils.copyProperties(session, response);
        return response;
    }
}
