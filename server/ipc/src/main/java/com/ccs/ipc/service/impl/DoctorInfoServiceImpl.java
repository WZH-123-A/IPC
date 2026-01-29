package com.ccs.ipc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ccs.ipc.dto.doctordto.*;
import com.ccs.ipc.dto.patientdto.DoctorSimpleResponse;
import com.ccs.ipc.common.exception.ApiException;
import com.ccs.ipc.dto.userinfodto.AdminDoctorInfoItem;
import com.ccs.ipc.dto.userinfodto.AdminDoctorInfoListRequest;
import com.ccs.ipc.dto.userinfodto.AdminDoctorInfoListResponse;
import com.ccs.ipc.dto.userinfodto.AdminDoctorInfoSaveRequest;
import com.ccs.ipc.dto.userinfodto.UserOptionItem;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

            // 查询患者信息
            if (session.getPatientId() != null) {
                SysUser patient = sysUserService.getById(session.getPatientId());
                if (patient != null) {
                    consultationResponse.setPatientName(patient.getRealName());
                    consultationResponse.setPatientAvatar(patient.getAvatar());
                    
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

    @Override
    public List<DoctorSimpleResponse> getAvailableDoctors() {
        // 查询医生角色ID
        SysRole doctorRole = sysRoleService.getOne(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, "doctor")
                .eq(SysRole::getIsDeleted, 0));
        
        if (doctorRole == null) {
            return new ArrayList<>();
        }

        // 查询拥有医生角色的用户ID列表
        List<SysUserRole> userRoles = sysUserRoleService.list(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getRoleId, doctorRole.getId()));
        
        if (userRoles == null || userRoles.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> doctorUserIds = userRoles.stream()
                .map(SysUserRole::getUserId)
                .collect(Collectors.toList());

        // 查询医生用户信息（只查询正常状态的）
        List<SysUser> doctors = sysUserService.list(new LambdaQueryWrapper<SysUser>()
                .in(SysUser::getId, doctorUserIds)
                .eq(SysUser::getIsDeleted, 0)
                .eq(SysUser::getStatus, 1)
                .orderByDesc(SysUser::getCreateTime));

        // 查询医生扩展信息
        List<DoctorInfo> doctorInfos = this.list(new LambdaQueryWrapper<DoctorInfo>()
                .in(DoctorInfo::getUserId, doctorUserIds)
                .eq(DoctorInfo::getIsDeleted, 0));

        // 构建医生信息Map（userId -> DoctorInfo）
        Map<Long, DoctorInfo> doctorInfoMap = doctorInfos.stream()
                .collect(Collectors.toMap(DoctorInfo::getUserId, info -> info, (k1, k2) -> k1));

        // 转换为响应DTO
        List<DoctorSimpleResponse> responseList = new ArrayList<>();
        for (SysUser doctor : doctors) {
            DoctorSimpleResponse response = new DoctorSimpleResponse();
            response.setId(doctor.getId());
            response.setRealName(doctor.getRealName());
            response.setAvatar(doctor.getAvatar());

            // 填充医生扩展信息
            DoctorInfo doctorInfo = doctorInfoMap.get(doctor.getId());
            if (doctorInfo != null) {
                response.setHospital(doctorInfo.getHospital());
                response.setDepartment(doctorInfo.getDepartment());
                response.setTitle(doctorInfo.getTitle());
                response.setSpecialty(doctorInfo.getSpecialty());
                response.setWorkYears(doctorInfo.getWorkYears());
            }

            responseList.add(response);
        }

        return responseList;
    }

    @Override
    public AdminDoctorInfoListResponse getDoctorInfoListForAdmin(AdminDoctorInfoListRequest request) {
        SysRole doctorRole = sysRoleService.getOne(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, "doctor")
                .eq(SysRole::getIsDeleted, 0));
        if (doctorRole == null) {
            AdminDoctorInfoListResponse empty = new AdminDoctorInfoListResponse();
            empty.setRecords(Collections.emptyList());
            empty.setTotal(0L);
            empty.setCurrent(request.getCurrent() != null ? request.getCurrent().longValue() : 1L);
            empty.setSize(request.getSize() != null ? request.getSize().longValue() : 10L);
            return empty;
        }
        List<SysUserRole> userRoles = sysUserRoleService.list(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getRoleId, doctorRole.getId()));
        Set<Long> doctorUserIds = userRoles.stream().map(SysUserRole::getUserId).collect(Collectors.toSet());
        if (doctorUserIds.isEmpty()) {
            AdminDoctorInfoListResponse empty = new AdminDoctorInfoListResponse();
            empty.setRecords(Collections.emptyList());
            empty.setTotal(0L);
            empty.setCurrent(request.getCurrent() != null ? request.getCurrent().longValue() : 1L);
            empty.setSize(request.getSize() != null ? request.getSize().longValue() : 10L);
            return empty;
        }
        if (StringUtils.hasText(request.getUsername()) || StringUtils.hasText(request.getRealName())) {
            LambdaQueryWrapper<SysUser> userQ = new LambdaQueryWrapper<SysUser>()
                    .in(SysUser::getId, doctorUserIds)
                    .eq(SysUser::getIsDeleted, 0);
            if (StringUtils.hasText(request.getUsername())) {
                userQ.like(SysUser::getUsername, request.getUsername());
            }
            if (StringUtils.hasText(request.getRealName())) {
                userQ.like(SysUser::getRealName, request.getRealName());
            }
            List<SysUser> filtered = sysUserService.list(userQ);
            doctorUserIds = filtered.stream().map(SysUser::getId).collect(Collectors.toSet());
            if (doctorUserIds.isEmpty()) {
                AdminDoctorInfoListResponse empty = new AdminDoctorInfoListResponse();
                empty.setRecords(Collections.emptyList());
                empty.setTotal(0L);
                empty.setCurrent(request.getCurrent() != null ? request.getCurrent().longValue() : 1L);
                empty.setSize(request.getSize() != null ? request.getSize().longValue() : 10L);
                return empty;
            }
        }
        long current = request.getCurrent() != null && request.getCurrent() > 0 ? request.getCurrent() : 1L;
        long size = request.getSize() != null && request.getSize() > 0 ? request.getSize() : 10L;
        LambdaQueryWrapper<DoctorInfo> q = new LambdaQueryWrapper<DoctorInfo>()
                .eq(DoctorInfo::getIsDeleted, 0)
                .in(DoctorInfo::getUserId, doctorUserIds);
        if (StringUtils.hasText(request.getHospital())) {
            q.like(DoctorInfo::getHospital, request.getHospital());
        }
        if (StringUtils.hasText(request.getDepartment())) {
            q.like(DoctorInfo::getDepartment, request.getDepartment());
        }
        q.orderByDesc(DoctorInfo::getCreateTime);
        Page<DoctorInfo> page = page(new Page<>(current, size), q);
        List<DoctorInfo> records = page.getRecords();
        Set<Long> userIds = records.stream().map(DoctorInfo::getUserId).collect(Collectors.toSet());
        Map<Long, SysUser> userMap = sysUserService.listByIds(userIds).stream().collect(Collectors.toMap(SysUser::getId, u -> u));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<AdminDoctorInfoItem> items = records.stream().map(d -> {
            AdminDoctorInfoItem item = new AdminDoctorInfoItem();
            item.setId(d.getId());
            item.setUserId(d.getUserId());
            item.setHospital(d.getHospital());
            item.setDepartment(d.getDepartment());
            item.setTitle(d.getTitle());
            item.setSpecialty(d.getSpecialty());
            item.setLicenseNo(d.getLicenseNo());
            item.setWorkYears(d.getWorkYears());
            item.setCreateTime(d.getCreateTime() == null ? null : d.getCreateTime().format(dtf));
            item.setUpdateTime(d.getUpdateTime() == null ? null : d.getUpdateTime().format(dtf));
            SysUser u = userMap.get(d.getUserId());
            if (u != null) {
                item.setUsername(u.getUsername());
                item.setRealName(u.getRealName());
                item.setPhone(u.getPhone());
            }
            return item;
        }).collect(Collectors.toList());
        AdminDoctorInfoListResponse response = new AdminDoctorInfoListResponse();
        response.setRecords(items);
        response.setTotal(page.getTotal());
        response.setCurrent(page.getCurrent());
        response.setSize(page.getSize());
        return response;
    }

    @Override
    public List<UserOptionItem> getDoctorUserOptionsForAdmin() {
        SysRole doctorRole = sysRoleService.getOne(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, "doctor")
                .eq(SysRole::getIsDeleted, 0));
        if (doctorRole == null) return Collections.emptyList();
        List<SysUserRole> userRoles = sysUserRoleService.list(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getRoleId, doctorRole.getId()));
        Set<Long> doctorUserIds = userRoles.stream().map(SysUserRole::getUserId).collect(Collectors.toSet());
        if (doctorUserIds.isEmpty()) return Collections.emptyList();
        List<DoctorInfo> existing = list(new LambdaQueryWrapper<DoctorInfo>()
                .eq(DoctorInfo::getIsDeleted, 0)
                .in(DoctorInfo::getUserId, doctorUserIds));
        Set<Long> hasInfoUserIds = existing.stream().map(DoctorInfo::getUserId).collect(Collectors.toSet());
        doctorUserIds.removeAll(hasInfoUserIds);
        if (doctorUserIds.isEmpty()) return Collections.emptyList();
        List<SysUser> users = sysUserService.list(new LambdaQueryWrapper<SysUser>()
                .in(SysUser::getId, doctorUserIds)
                .eq(SysUser::getIsDeleted, 0));
        return users.stream().map(u -> {
            UserOptionItem item = new UserOptionItem();
            item.setId(u.getId());
            item.setUsername(u.getUsername());
            item.setRealName(u.getRealName());
            return item;
        }).collect(Collectors.toList());
    }

    @Override
    public void createDoctorInfoForAdmin(AdminDoctorInfoSaveRequest request) {
        SysRole doctorRole = sysRoleService.getOne(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, "doctor")
                .eq(SysRole::getIsDeleted, 0));
        if (doctorRole == null) throw new ApiException("未找到医生角色");
        long count = sysUserRoleService.count(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, request.getUserId())
                .eq(SysUserRole::getRoleId, doctorRole.getId()));
        if (count == 0) throw new ApiException("该用户不是医生角色");
        long exist = count(new LambdaQueryWrapper<DoctorInfo>()
                .eq(DoctorInfo::getUserId, request.getUserId())
                .eq(DoctorInfo::getIsDeleted, 0));
        if (exist > 0) throw new ApiException("该用户已填写医生信息");
        DoctorInfo entity = new DoctorInfo();
        entity.setUserId(request.getUserId());
        entity.setHospital(request.getHospital());
        entity.setDepartment(request.getDepartment());
        entity.setTitle(request.getTitle());
        entity.setSpecialty(request.getSpecialty());
        entity.setLicenseNo(request.getLicenseNo());
        entity.setWorkYears(request.getWorkYears());
        entity.setIsDeleted((byte) 0);
        save(entity);
    }

    @Override
    public void updateDoctorInfoForAdmin(Long id, AdminDoctorInfoSaveRequest request) {
        DoctorInfo entity = getById(id);
        if (entity == null) throw new ApiException("医生信息不存在");
        entity.setHospital(request.getHospital());
        entity.setDepartment(request.getDepartment());
        entity.setTitle(request.getTitle());
        entity.setSpecialty(request.getSpecialty());
        entity.setLicenseNo(request.getLicenseNo());
        entity.setWorkYears(request.getWorkYears());
        updateById(entity);
    }

    @Override
    public void deleteDoctorInfoForAdmin(Long id) {
        DoctorInfo entity = getById(id);
        if (entity == null) throw new ApiException("医生信息不存在");
        entity.setIsDeleted((byte) 1);
        updateById(entity);
    }
}
