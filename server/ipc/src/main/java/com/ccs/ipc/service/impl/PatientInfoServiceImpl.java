package com.ccs.ipc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ccs.ipc.common.exception.ApiException;
import com.ccs.ipc.dto.userinfodto.AdminPatientInfoItem;
import com.ccs.ipc.dto.userinfodto.AdminPatientInfoListRequest;
import com.ccs.ipc.dto.userinfodto.AdminPatientInfoListResponse;
import com.ccs.ipc.dto.userinfodto.AdminPatientInfoSaveRequest;
import com.ccs.ipc.dto.userinfodto.UserOptionItem;
import com.ccs.ipc.entity.PatientInfo;
import com.ccs.ipc.entity.SysRole;
import com.ccs.ipc.entity.SysUser;
import com.ccs.ipc.entity.SysUserRole;
import com.ccs.ipc.mapper.PatientInfoMapper;
import com.ccs.ipc.service.IPatientInfoService;
import com.ccs.ipc.service.ISysRoleService;
import com.ccs.ipc.service.ISysUserRoleService;
import com.ccs.ipc.service.ISysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 患者扩展信息表 服务实现类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
@Service
public class PatientInfoServiceImpl extends ServiceImpl<PatientInfoMapper, PatientInfo> implements IPatientInfoService {

    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private ISysRoleService sysRoleService;
    @Autowired
    private ISysUserRoleService sysUserRoleService;

    @Override
    public AdminPatientInfoListResponse getPatientInfoListForAdmin(AdminPatientInfoListRequest request) {
        SysRole patientRole = sysRoleService.getOne(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, "patient")
                .eq(SysRole::getIsDeleted, 0));
        if (patientRole == null) {
            return emptyPatientResponse(request);
        }
        List<SysUserRole> userRoles = sysUserRoleService.list(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getRoleId, patientRole.getId()));
        Set<Long> patientUserIds = userRoles.stream().map(SysUserRole::getUserId).collect(Collectors.toSet());
        if (patientUserIds.isEmpty()) {
            return emptyPatientResponse(request);
        }
        if (StringUtils.hasText(request.getUsername()) || StringUtils.hasText(request.getRealName())) {
            LambdaQueryWrapper<SysUser> userQ = new LambdaQueryWrapper<SysUser>()
                    .in(SysUser::getId, patientUserIds)
                    .eq(SysUser::getIsDeleted, 0);
            if (StringUtils.hasText(request.getUsername())) {
                userQ.like(SysUser::getUsername, request.getUsername());
            }
            if (StringUtils.hasText(request.getRealName())) {
                userQ.like(SysUser::getRealName, request.getRealName());
            }
            List<SysUser> filtered = sysUserService.list(userQ);
            patientUserIds = filtered.stream().map(SysUser::getId).collect(Collectors.toSet());
            if (patientUserIds.isEmpty()) {
                return emptyPatientResponse(request);
            }
        }
        long current = request.getCurrent() != null && request.getCurrent() > 0 ? request.getCurrent() : 1L;
        long size = request.getSize() != null && request.getSize() > 0 ? request.getSize() : 10L;
        LambdaQueryWrapper<PatientInfo> q = new LambdaQueryWrapper<PatientInfo>()
                .eq(PatientInfo::getIsDeleted, 0)
                .in(PatientInfo::getUserId, patientUserIds);
        if (request.getAge() != null) {
            q.eq(PatientInfo::getAge, request.getAge());
        }
        q.orderByDesc(PatientInfo::getCreateTime);
        Page<PatientInfo> page = this.page(new Page<>(current, size), q);
        List<PatientInfo> records = page.getRecords();
        Set<Long> userIds = records.stream().map(PatientInfo::getUserId).collect(Collectors.toSet());
        Map<Long, SysUser> userMap = sysUserService.listByIds(userIds).stream().collect(Collectors.toMap(SysUser::getId, u -> u));
        List<AdminPatientInfoItem> items = records.stream().map(p -> {
            AdminPatientInfoItem item = new AdminPatientInfoItem();
            item.setId(p.getId());
            item.setUserId(p.getUserId());
            item.setAge(p.getAge());
            item.setAddress(p.getAddress());
            item.setMedicalHistory(p.getMedicalHistory());
            item.setAllergyHistory(p.getAllergyHistory());
            item.setCreateTime(p.getCreateTime() == null ? null : p.getCreateTime().format(DATETIME_FMT));
            item.setUpdateTime(p.getUpdateTime() == null ? null : p.getUpdateTime().format(DATETIME_FMT));
            SysUser u = userMap.get(p.getUserId());
            if (u != null) {
                item.setUsername(u.getUsername());
                item.setRealName(u.getRealName());
                item.setPhone(u.getPhone());
            }
            return item;
        }).collect(Collectors.toList());
        AdminPatientInfoListResponse response = new AdminPatientInfoListResponse();
        response.setRecords(items);
        response.setTotal(page.getTotal());
        response.setCurrent(page.getCurrent());
        response.setSize(page.getSize());
        return response;
    }

    private AdminPatientInfoListResponse emptyPatientResponse(AdminPatientInfoListRequest request) {
        AdminPatientInfoListResponse empty = new AdminPatientInfoListResponse();
        empty.setRecords(Collections.emptyList());
        empty.setTotal(0L);
        empty.setCurrent(request.getCurrent() != null ? request.getCurrent().longValue() : 1L);
        empty.setSize(request.getSize() != null ? request.getSize().longValue() : 10L);
        return empty;
    }

    @Override
    public List<UserOptionItem> getPatientUserOptionsForAdmin() {
        SysRole patientRole = sysRoleService.getOne(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, "patient")
                .eq(SysRole::getIsDeleted, 0));
        if (patientRole == null) return Collections.emptyList();
        List<SysUserRole> userRoles = sysUserRoleService.list(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getRoleId, patientRole.getId()));
        Set<Long> patientUserIds = userRoles.stream().map(SysUserRole::getUserId).collect(Collectors.toSet());
        if (patientUserIds.isEmpty()) return Collections.emptyList();
        List<PatientInfo> existing = list(new LambdaQueryWrapper<PatientInfo>()
                .eq(PatientInfo::getIsDeleted, 0)
                .in(PatientInfo::getUserId, patientUserIds));
        Set<Long> hasInfoUserIds = existing.stream().map(PatientInfo::getUserId).collect(Collectors.toSet());
        patientUserIds.removeAll(hasInfoUserIds);
        if (patientUserIds.isEmpty()) return Collections.emptyList();
        List<SysUser> users = sysUserService.list(new LambdaQueryWrapper<SysUser>()
                .in(SysUser::getId, patientUserIds)
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
    public void createPatientInfoForAdmin(AdminPatientInfoSaveRequest request) {
        SysRole patientRole = sysRoleService.getOne(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, "patient")
                .eq(SysRole::getIsDeleted, 0));
        if (patientRole == null) throw new ApiException("未找到患者角色");
        long count = sysUserRoleService.count(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, request.getUserId())
                .eq(SysUserRole::getRoleId, patientRole.getId()));
        if (count == 0) throw new ApiException("该用户不是患者角色");
        long exist = count(new LambdaQueryWrapper<PatientInfo>()
                .eq(PatientInfo::getUserId, request.getUserId())
                .eq(PatientInfo::getIsDeleted, 0));
        if (exist > 0) throw new ApiException("该用户已填写患者信息");
        PatientInfo entity = new PatientInfo();
        entity.setUserId(request.getUserId());
        entity.setAge(request.getAge());
        entity.setAddress(request.getAddress());
        entity.setMedicalHistory(request.getMedicalHistory());
        entity.setAllergyHistory(request.getAllergyHistory());
        entity.setIsDeleted((byte) 0);
        save(entity);
    }

    @Override
    public void updatePatientInfoForAdmin(Long id, AdminPatientInfoSaveRequest request) {
        PatientInfo entity = getById(id);
        if (entity == null) throw new ApiException("患者信息不存在");
        entity.setAge(request.getAge());
        entity.setAddress(request.getAddress());
        entity.setMedicalHistory(request.getMedicalHistory());
        entity.setAllergyHistory(request.getAllergyHistory());
        updateById(entity);
    }

    @Override
    public void deletePatientInfoForAdmin(Long id) {
        PatientInfo entity = getById(id);
        if (entity == null) throw new ApiException("患者信息不存在");
        entity.setIsDeleted((byte) 1);
        updateById(entity);
    }
}
