package com.ccs.ipc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ccs.ipc.dto.admindto.AdminConsultationEvaluationListRequest;
import com.ccs.ipc.dto.admindto.AdminConsultationEvaluationListResponse;
import com.ccs.ipc.dto.admindto.AdminConsultationEvaluationResponse;
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
}
