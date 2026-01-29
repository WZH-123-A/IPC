package com.ccs.ipc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ccs.ipc.dto.admindto.AdminConsultationSessionListRequest;
import com.ccs.ipc.dto.admindto.AdminConsultationSessionListResponse;
import com.ccs.ipc.dto.admindto.AdminConsultationSessionResponse;
import com.ccs.ipc.dto.admindto.AdminConsultationSessionUpdateRequest;
import com.ccs.ipc.dto.patientdto.ConsultationSessionListRequest;
import com.ccs.ipc.dto.patientdto.ConsultationSessionListResponse;
import com.ccs.ipc.dto.patientdto.ConsultationSessionResponse;
import com.ccs.ipc.dto.patientdto.CreateConsultationSessionRequest;
import com.ccs.ipc.entity.ConsultationSession;
import com.ccs.ipc.entity.SysUser;
import com.ccs.ipc.mapper.ConsultationSessionMapper;
import com.ccs.ipc.service.IConsultationSessionService;
import com.ccs.ipc.service.ISysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    @Autowired
    private ISysUserService sysUserService;

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

    @Override
    public AdminConsultationSessionListResponse getAdminSessionList(AdminConsultationSessionListRequest request) {
        Page<ConsultationSession> page = new Page<>(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<ConsultationSession> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ConsultationSession::getIsDeleted, 0);

        // 条件查询
        if (StringUtils.hasText(request.getSessionNo())) {
            queryWrapper.like(ConsultationSession::getSessionNo, request.getSessionNo());
        }
        if (request.getPatientId() != null) {
            queryWrapper.eq(ConsultationSession::getPatientId, request.getPatientId());
        }
        if (request.getDoctorId() != null) {
            queryWrapper.eq(ConsultationSession::getDoctorId, request.getDoctorId());
        }
        if (request.getSessionType() != null) {
            queryWrapper.eq(ConsultationSession::getSessionType, request.getSessionType());
        }
        if (request.getStatus() != null) {
            queryWrapper.eq(ConsultationSession::getStatus, request.getStatus());
        }

        queryWrapper.orderByDesc(ConsultationSession::getCreateTime);
        Page<ConsultationSession> result = this.page(page, queryWrapper);

        AdminConsultationSessionListResponse response = new AdminConsultationSessionListResponse();
        response.setTotal(result.getTotal());
        response.setCurrent(result.getCurrent());
        response.setSize(result.getSize());

        // 获取所有患者和医生的ID
        Set<Long> patientIds = result.getRecords().stream()
                .map(ConsultationSession::getPatientId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        Set<Long> doctorIds = result.getRecords().stream()
                .map(ConsultationSession::getDoctorId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        // 批量查询用户信息
        Map<Long, String> patientNameMap = patientIds.isEmpty() ? Map.of() :
                sysUserService.listByIds(patientIds).stream()
                        .collect(Collectors.toMap(SysUser::getId, user -> 
                                StringUtils.hasText(user.getRealName()) ? user.getRealName() : user.getUsername(), 
                                (v1, v2) -> v1));
        Map<Long, String> doctorNameMap = doctorIds.isEmpty() ? Map.of() :
                sysUserService.listByIds(doctorIds).stream()
                        .collect(Collectors.toMap(SysUser::getId, user -> 
                                StringUtils.hasText(user.getRealName()) ? user.getRealName() : user.getUsername(), 
                                (v1, v2) -> v1));

        // 转换为响应DTO
        List<AdminConsultationSessionResponse> responseList = result.getRecords().stream()
                .map(session -> convertToAdminResponse(session, patientNameMap, doctorNameMap))
                .collect(Collectors.toList());
        response.setRecords(responseList);

        return response;
    }

    @Override
    public AdminConsultationSessionResponse getAdminSessionById(Long id) {
        ConsultationSession session = this.getById(id);
        if (session == null || session.getIsDeleted() == 1) {
            throw new RuntimeException("问诊会话不存在");
        }

        // 获取患者和医生名称
        Map<Long, String> patientNameMap = Map.of();
        Map<Long, String> doctorNameMap = Map.of();

        if (session.getPatientId() != null) {
            SysUser patient = sysUserService.getById(session.getPatientId());
            if (patient != null) {
                String patientName = StringUtils.hasText(patient.getRealName()) ? patient.getRealName() : patient.getUsername();
                patientNameMap = Map.of(session.getPatientId(), patientName);
            }
        }

        if (session.getDoctorId() != null) {
            SysUser doctor = sysUserService.getById(session.getDoctorId());
            if (doctor != null) {
                String doctorName = StringUtils.hasText(doctor.getRealName()) ? doctor.getRealName() : doctor.getUsername();
                doctorNameMap = Map.of(session.getDoctorId(), doctorName);
            }
        }

        return convertToAdminResponse(session, patientNameMap, doctorNameMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAdminSession(Long id, AdminConsultationSessionUpdateRequest request) {
        ConsultationSession session = this.getById(id);
        if (session == null || session.getIsDeleted() == 1) {
            throw new RuntimeException("问诊会话不存在");
        }
        if (request.getTitle() != null) {
            session.setTitle(request.getTitle());
        }
        if (request.getStatus() != null) {
            session.setStatus(request.getStatus());
            if (request.getStatus() == 1) {
                session.setEndTime(LocalDateTime.now());
            }
        }
        this.updateById(session);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAdminSession(Long id) {
        ConsultationSession session = this.getById(id);
        if (session == null || session.getIsDeleted() == 1) {
            throw new RuntimeException("问诊会话不存在");
        }
        session.setIsDeleted((byte) 1);
        this.updateById(session);
    }

    private AdminConsultationSessionResponse convertToAdminResponse(ConsultationSession session, 
                                                               Map<Long, String> patientNameMap, 
                                                               Map<Long, String> doctorNameMap) {
        AdminConsultationSessionResponse response = new AdminConsultationSessionResponse();
        BeanUtils.copyProperties(session, response);
        
        if (session.getPatientId() != null) {
            response.setPatientName(patientNameMap.getOrDefault(session.getPatientId(), "未知患者"));
        }
        
        if (session.getDoctorId() != null) {
            response.setDoctorName(doctorNameMap.getOrDefault(session.getDoctorId(), "未知医生"));
        } else if (session.getSessionType() == 1) {
            response.setDoctorName("AI问诊");
        }
        
        return response;
    }
}
