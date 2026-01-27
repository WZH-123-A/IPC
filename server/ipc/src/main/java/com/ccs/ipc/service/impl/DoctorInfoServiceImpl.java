package com.ccs.ipc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ccs.ipc.dto.doctordto.*;
import com.ccs.ipc.entity.*;
import com.ccs.ipc.mapper.DoctorInfoMapper;
import com.ccs.ipc.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 医生扩展信息表 服务实现类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
@Service
public class DoctorInfoServiceImpl extends ServiceImpl<DoctorInfoMapper, DoctorInfo> implements IDoctorInfoService {

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ISysRoleService sysRoleService;

    @Autowired
    private ISysUserRoleService sysUserRoleService;

    @Autowired
    private IPatientInfoService patientInfoService;

    @Autowired
    private IConsultationSessionService consultationSessionService;

    @Autowired
    private IConsultationMessageService consultationMessageService;

    @Override
    public DoctorHomeResponse getDoctorHome(Long doctorId) {
        DoctorHomeResponse response = new DoctorHomeResponse();

        // 获取医生信息
        DoctorInfo doctorInfo = this.getOne(new LambdaQueryWrapper<DoctorInfo>()
                .eq(DoctorInfo::getUserId, doctorId)
                .eq(DoctorInfo::getIsDeleted, 0));
        
        SysUser user = sysUserService.getById(doctorId);
        
        DoctorInfoResponse doctorInfoResponse = new DoctorInfoResponse();
        if (user != null) {
            BeanUtils.copyProperties(user, doctorInfoResponse);
        }
        if (doctorInfo != null) {
            doctorInfoResponse.setHospital(doctorInfo.getHospital());
            doctorInfoResponse.setDepartment(doctorInfo.getDepartment());
            doctorInfoResponse.setTitle(doctorInfo.getTitle());
            doctorInfoResponse.setSpecialty(doctorInfo.getSpecialty());
            doctorInfoResponse.setLicenseNo(doctorInfo.getLicenseNo());
            doctorInfoResponse.setWorkYears(doctorInfo.getWorkYears());
        }
        response.setDoctorInfo(doctorInfoResponse);

        // 统计今日问诊数
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        Long todayCount = consultationSessionService.count(new LambdaQueryWrapper<ConsultationSession>()
                .eq(ConsultationSession::getDoctorId, doctorId)
                .eq(ConsultationSession::getIsDeleted, 0)
                .ge(ConsultationSession::getCreateTime, todayStart)
                .le(ConsultationSession::getCreateTime, todayEnd));
        response.setTodayConsultationCount(todayCount);

        // 统计进行中的问诊数
        Long ongoingCount = consultationSessionService.count(new LambdaQueryWrapper<ConsultationSession>()
                .eq(ConsultationSession::getDoctorId, doctorId)
                .eq(ConsultationSession::getStatus, 0)
                .eq(ConsultationSession::getIsDeleted, 0));
        response.setOngoingConsultationCount(ongoingCount);

        // 统计总患者数（与该医生有过问诊的患者）
        List<ConsultationSession> sessions = consultationSessionService.list(new LambdaQueryWrapper<ConsultationSession>()
                .eq(ConsultationSession::getDoctorId, doctorId)
                .eq(ConsultationSession::getIsDeleted, 0)
                .select(ConsultationSession::getPatientId));
        long totalPatientCount = sessions.stream()
                .map(ConsultationSession::getPatientId)
                .distinct()
                .count();
        response.setTotalPatientCount(totalPatientCount);

        // 统计本月问诊数
        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDateTime monthStart = LocalDateTime.of(firstDayOfMonth, LocalTime.MIN);
        Long monthCount = consultationSessionService.count(new LambdaQueryWrapper<ConsultationSession>()
                .eq(ConsultationSession::getDoctorId, doctorId)
                .eq(ConsultationSession::getIsDeleted, 0)
                .ge(ConsultationSession::getCreateTime, monthStart));
        response.setMonthConsultationCount(monthCount);

        return response;
    }

    @Override
    public PatientListResponse getPatientList(Long doctorId, PatientListRequest request) {
        // 确保分页参数有效
        long current = request.getCurrent() != null && request.getCurrent() > 0 ? request.getCurrent() : 1L;
        long size = request.getSize() != null && request.getSize() > 0 ? request.getSize() : 10L;

        // 查询患者角色ID
        SysRole patientRole = sysRoleService.getOne(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, "patient")
                .eq(SysRole::getIsDeleted, 0));
        
        if (patientRole == null) {
            PatientListResponse emptyResponse = new PatientListResponse();
            emptyResponse.setRecords(new ArrayList<>());
            emptyResponse.setTotal(0L);
            emptyResponse.setCurrent(current);
            emptyResponse.setSize(size);
            return emptyResponse;
        }

        // 查询拥有患者角色的用户ID列表
        List<SysUserRole> userRoles = sysUserRoleService.list(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getRoleId, patientRole.getId()));
        
        if (userRoles == null || userRoles.isEmpty()) {
            PatientListResponse emptyResponse = new PatientListResponse();
            emptyResponse.setRecords(new ArrayList<>());
            emptyResponse.setTotal(0L);
            emptyResponse.setCurrent(current);
            emptyResponse.setSize(size);
            return emptyResponse;
        }

        List<Long> patientUserIds = userRoles.stream()
                .map(SysUserRole::getUserId)
                .collect(Collectors.toList());

        // 构建查询条件
        Page<SysUser> page = new Page<>(current, size);
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysUser::getId, patientUserIds)
                .eq(SysUser::getIsDeleted, 0)
                .eq(SysUser::getStatus, 1);

        if (StringUtils.hasText(request.getRealName())) {
            queryWrapper.like(SysUser::getRealName, request.getRealName());
        }
        if (StringUtils.hasText(request.getUsername())) {
            queryWrapper.like(SysUser::getUsername, request.getUsername());
        }
        if (StringUtils.hasText(request.getPhone())) {
            queryWrapper.like(SysUser::getPhone, request.getPhone());
        }
        queryWrapper.orderByDesc(SysUser::getCreateTime);

        // 执行分页查询
        Page<SysUser> result = sysUserService.page(page, queryWrapper);

        // 转换为Response
        PatientListResponse response = new PatientListResponse();
        response.setTotal(result.getTotal());
        response.setCurrent(result.getCurrent());
        response.setSize(result.getSize());

        List<PatientResponse> responseList = new ArrayList<>();
        for (SysUser user : result.getRecords()) {
            PatientResponse patientResponse = new PatientResponse();
            BeanUtils.copyProperties(user, patientResponse);

            // 查询患者扩展信息
            PatientInfo patientInfo = patientInfoService.getOne(new LambdaQueryWrapper<PatientInfo>()
                    .eq(PatientInfo::getUserId, user.getId())
                    .eq(PatientInfo::getIsDeleted, 0));
            
            if (patientInfo != null) {
                patientResponse.setAge(patientInfo.getAge());
                patientResponse.setAddress(patientInfo.getAddress());
                patientResponse.setMedicalHistory(patientInfo.getMedicalHistory());
                patientResponse.setAllergyHistory(patientInfo.getAllergyHistory());
            }

            responseList.add(patientResponse);
        }
        response.setRecords(responseList);

        return response;
    }

    @Override
    public ConsultationListResponse getConsultationList(Long doctorId, ConsultationListRequest request) {
        // 确保分页参数有效
        long current = request.getCurrent() != null && request.getCurrent() > 0 ? request.getCurrent() : 1L;
        long size = request.getSize() != null && request.getSize() > 0 ? request.getSize() : 10L;

        Page<ConsultationSession> page = new Page<>(current, size);
        LambdaQueryWrapper<ConsultationSession> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ConsultationSession::getDoctorId, doctorId)
                .eq(ConsultationSession::getIsDeleted, 0);

        if (StringUtils.hasText(request.getTitle())) {
            queryWrapper.like(ConsultationSession::getTitle, request.getTitle());
        }
        if (request.getStatus() != null) {
            queryWrapper.eq(ConsultationSession::getStatus, request.getStatus());
        }
        if (request.getSessionType() != null) {
            queryWrapper.eq(ConsultationSession::getSessionType, request.getSessionType());
        }
        if (request.getStartTime() != null) {
            queryWrapper.ge(ConsultationSession::getStartTime, request.getStartTime());
        }
        if (request.getEndTime() != null) {
            queryWrapper.le(ConsultationSession::getEndTime, request.getEndTime());
        }
        // 注意：患者姓名过滤在结果处理时进行，因为需要关联查询用户表
        queryWrapper.orderByDesc(ConsultationSession::getCreateTime);

        // 执行分页查询
        Page<ConsultationSession> result = consultationSessionService.page(page, queryWrapper);

        // 转换为Response
        ConsultationListResponse response = new ConsultationListResponse();
        response.setTotal(result.getTotal());
        response.setCurrent(result.getCurrent());
        response.setSize(result.getSize());

        List<ConsultationResponse> responseList = new ArrayList<>();
        for (ConsultationSession session : result.getRecords()) {
            ConsultationResponse consultationResponse = new ConsultationResponse();
            BeanUtils.copyProperties(session, consultationResponse);

            // 查询患者姓名
            if (session.getPatientId() != null) {
                SysUser patient = sysUserService.getById(session.getPatientId());
                if (patient != null) {
                    consultationResponse.setPatientName(patient.getRealName());
                    
                    // 如果指定了患者姓名过滤，检查是否匹配
                    if (StringUtils.hasText(request.getPatientName())) {
                        if (!patient.getRealName().contains(request.getPatientName())) {
                            continue; // 如果患者姓名不匹配，跳过这条记录
                        }
                    }
                }
            }

            // 统计未读消息数（患者发送的未读消息）
            Long unreadCount = consultationMessageService.count(new LambdaQueryWrapper<ConsultationMessage>()
                    .eq(ConsultationMessage::getSessionId, session.getId())
                    .eq(ConsultationMessage::getSenderType, 1) // 患者发送
                    .eq(ConsultationMessage::getIsRead, 0)
                    .eq(ConsultationMessage::getIsDeleted, 0));
            consultationResponse.setUnreadCount(unreadCount != null ? unreadCount.intValue() : 0);

            responseList.add(consultationResponse);
        }
        response.setRecords(responseList);

        return response;
    }
}
