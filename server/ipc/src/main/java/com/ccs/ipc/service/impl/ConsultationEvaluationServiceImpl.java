package com.ccs.ipc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ccs.ipc.dto.admindto.AdminConsultationEvaluationCreateRequest;
import com.ccs.ipc.dto.admindto.AdminConsultationEvaluationListRequest;
import com.ccs.ipc.dto.admindto.AdminConsultationEvaluationListResponse;
import com.ccs.ipc.dto.admindto.AdminConsultationEvaluationResponse;
import com.ccs.ipc.dto.admindto.AdminConsultationEvaluationUpdateRequest;
import com.ccs.ipc.dto.patientdto.EvaluationResponse;
import com.ccs.ipc.dto.patientdto.SubmitEvaluationRequest;
import com.ccs.ipc.entity.ConsultationEvaluation;
import com.ccs.ipc.entity.ConsultationSession;
import com.ccs.ipc.entity.SysUser;
import com.ccs.ipc.mapper.ConsultationEvaluationMapper;
import com.ccs.ipc.service.IConsultationEvaluationService;
import com.ccs.ipc.service.IConsultationSessionService;
import com.ccs.ipc.service.ISysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 问诊评价表 服务实现类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
@Service
public class ConsultationEvaluationServiceImpl extends ServiceImpl<ConsultationEvaluationMapper, ConsultationEvaluation> implements IConsultationEvaluationService {

    @Autowired
    private IConsultationSessionService consultationSessionService;

    @Autowired
    private ISysUserService sysUserService;

    @Override
    public AdminConsultationEvaluationListResponse getAdminEvaluationList(AdminConsultationEvaluationListRequest request) {
        LambdaQueryWrapper<ConsultationEvaluation> qw = new LambdaQueryWrapper<>();
        qw.eq(ConsultationEvaluation::getIsDeleted, 0);
        if (request.getSessionId() != null) {
            qw.eq(ConsultationEvaluation::getSessionId, request.getSessionId());
        }
        if (request.getPatientId() != null) {
            qw.eq(ConsultationEvaluation::getPatientId, request.getPatientId());
        }
        if (request.getDoctorId() != null) {
            qw.eq(ConsultationEvaluation::getDoctorId, request.getDoctorId());
        }
        if (request.getRating() != null) {
            qw.eq(ConsultationEvaluation::getRating, request.getRating());
        }
        qw.orderByDesc(ConsultationEvaluation::getCreateTime);

        Page<ConsultationEvaluation> page = new Page<>(request.getCurrent(), request.getSize());
        Page<ConsultationEvaluation> result = this.page(page, qw);

        List<ConsultationEvaluation> records = result.getRecords();
        List<AdminConsultationEvaluationResponse> list = new ArrayList<>();

        Set<Long> sessionIds = records.stream().map(ConsultationEvaluation::getSessionId).collect(Collectors.toSet());
        Set<Long> userIds = records.stream().map(ConsultationEvaluation::getPatientId).collect(Collectors.toSet());
        records.stream().map(ConsultationEvaluation::getDoctorId).filter(java.util.Objects::nonNull).forEach(userIds::add);

        Map<Long, ConsultationSession> sessionMap = consultationSessionService.listByIds(sessionIds).stream()
                .collect(Collectors.toMap(ConsultationSession::getId, s -> s));
        Map<Long, SysUser> userMap = sysUserService.listByIds(userIds).stream()
                .collect(Collectors.toMap(SysUser::getId, u -> u));

        for (ConsultationEvaluation e : records) {
            AdminConsultationEvaluationResponse resp = new AdminConsultationEvaluationResponse();
            resp.setId(e.getId());
            resp.setSessionId(e.getSessionId());
            resp.setPatientId(e.getPatientId());
            resp.setDoctorId(e.getDoctorId());
            resp.setRating(e.getRating());
            resp.setComment(e.getComment());
            resp.setCreateTime(e.getCreateTime());

            ConsultationSession session = sessionMap.get(e.getSessionId());
            resp.setSessionNo(session != null ? session.getSessionNo() : null);

            SysUser patient = userMap.get(e.getPatientId());
            resp.setPatientName(patient != null ? (StringUtils.hasText(patient.getRealName()) ? patient.getRealName() : patient.getUsername()) : null);
            if (e.getDoctorId() != null) {
                SysUser doctor = userMap.get(e.getDoctorId());
                resp.setDoctorName(doctor != null ? (StringUtils.hasText(doctor.getRealName()) ? doctor.getRealName() : doctor.getUsername()) : "-");
            } else {
                resp.setDoctorName("AI问诊");
            }
            list.add(resp);
        }

        AdminConsultationEvaluationListResponse response = new AdminConsultationEvaluationListResponse();
        response.setRecords(list);
        response.setTotal(result.getTotal());
        response.setCurrent(result.getCurrent());
        response.setSize(result.getSize());
        return response;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public AdminConsultationEvaluationResponse createAdminEvaluation(AdminConsultationEvaluationCreateRequest request) {
        ConsultationSession session = consultationSessionService.getById(request.getSessionId());
        if (session == null || session.getIsDeleted() == 1) {
            throw new RuntimeException("问诊会话不存在");
        }
        if (!session.getPatientId().equals(request.getPatientId())) {
            throw new RuntimeException("患者ID与会话不匹配");
        }
        ConsultationEvaluation e = new ConsultationEvaluation();
        e.setSessionId(request.getSessionId());
        e.setPatientId(request.getPatientId());
        e.setDoctorId(request.getDoctorId());
        e.setRating(request.getRating());
        e.setComment(request.getComment());
        e.setIsDeleted((byte) 0);
        this.save(e);
        AdminConsultationEvaluationResponse resp = new AdminConsultationEvaluationResponse();
        resp.setId(e.getId());
        resp.setSessionId(e.getSessionId());
        resp.setPatientId(e.getPatientId());
        resp.setDoctorId(e.getDoctorId());
        resp.setRating(e.getRating());
        resp.setComment(e.getComment());
        resp.setCreateTime(e.getCreateTime());
        ConsultationSession s = consultationSessionService.getById(e.getSessionId());
        resp.setSessionNo(s != null ? s.getSessionNo() : null);
        SysUser patient = sysUserService.getById(e.getPatientId());
        resp.setPatientName(patient != null ? (StringUtils.hasText(patient.getRealName()) ? patient.getRealName() : patient.getUsername()) : null);
        if (e.getDoctorId() != null) {
            SysUser doctor = sysUserService.getById(e.getDoctorId());
            resp.setDoctorName(doctor != null ? (StringUtils.hasText(doctor.getRealName()) ? doctor.getRealName() : doctor.getUsername()) : "-");
        } else {
            resp.setDoctorName("AI问诊");
        }
        return resp;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public void updateAdminEvaluation(Long id, AdminConsultationEvaluationUpdateRequest request) {
        ConsultationEvaluation e = this.getById(id);
        if (e == null || e.getIsDeleted() == 1) {
            throw new RuntimeException("问诊评价不存在");
        }
        if (request.getRating() != null) {
            e.setRating(request.getRating());
        }
        if (request.getComment() != null) {
            e.setComment(request.getComment());
        }
        this.updateById(e);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public void deleteAdminEvaluation(Long id) {
        ConsultationEvaluation e = this.getById(id);
        if (e == null || e.getIsDeleted() == 1) {
            throw new RuntimeException("问诊评价不存在");
        }
        e.setIsDeleted((byte) 1);
        this.updateById(e);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public EvaluationResponse submitPatientEvaluation(Long sessionId, Long patientId, SubmitEvaluationRequest request) {
        ConsultationSession session = consultationSessionService.getById(sessionId);
        if (session == null || session.getIsDeleted() == 1) {
            throw new RuntimeException("问诊会话不存在");
        }
        if (!session.getPatientId().equals(patientId)) {
            throw new RuntimeException("无权对该会话进行评价");
        }
        if (session.getStatus() == null || session.getStatus() != 1) {
            throw new RuntimeException("仅能对已结束的问诊进行评价");
        }
        long count = this.count(new LambdaQueryWrapper<ConsultationEvaluation>()
                .eq(ConsultationEvaluation::getSessionId, sessionId)
                .eq(ConsultationEvaluation::getIsDeleted, 0));
        if (count > 0) {
            throw new RuntimeException("该问诊已评价过，不可重复评价");
        }
        ConsultationEvaluation e = new ConsultationEvaluation();
        e.setSessionId(sessionId);
        e.setPatientId(patientId);
        e.setDoctorId(session.getSessionType() != null && session.getSessionType() == 2 ? session.getDoctorId() : null);
        e.setRating(request.getRating());
        e.setComment(request.getComment());
        e.setIsDeleted((byte) 0);
        this.save(e);
        return toEvaluationResponse(e);
    }

    @Override
    public EvaluationResponse getBySessionId(Long sessionId) {
        ConsultationEvaluation e = this.getOne(new LambdaQueryWrapper<ConsultationEvaluation>()
                .eq(ConsultationEvaluation::getSessionId, sessionId)
                .eq(ConsultationEvaluation::getIsDeleted, 0)
                .last("LIMIT 1"));
        if (e == null) {
            return null;
        }
        return toEvaluationResponse(e);
    }

    private EvaluationResponse toEvaluationResponse(ConsultationEvaluation e) {
        EvaluationResponse resp = new EvaluationResponse();
        resp.setId(e.getId());
        resp.setSessionId(e.getSessionId());
        resp.setRating(e.getRating());
        resp.setComment(e.getComment());
        resp.setCreateTime(e.getCreateTime());
        return resp;
    }
}
