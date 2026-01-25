package com.ccs.ipc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ccs.ipc.common.exception.ApiException;
import com.ccs.ipc.common.response.ResultCode;
import com.ccs.ipc.dto.roledto.*;
import com.ccs.ipc.entity.SysRole;
import com.ccs.ipc.entity.SysRolePermission;
import com.ccs.ipc.mapper.SysRoleMapper;
import com.ccs.ipc.service.ISysRolePermissionService;
import com.ccs.ipc.service.ISysRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统角色表 服务实现类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

    @Autowired
    private ISysRolePermissionService sysRolePermissionService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysRoleResponse createRole(CreateRoleRequest request) {
        // 检查角色编码是否已存在
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getRoleCode, request.getRoleCode())
                .eq(SysRole::getIsDeleted, 0);
        SysRole existRole = this.getOne(queryWrapper);
        if (existRole != null) {
            throw new ApiException(ResultCode.ROLE_CODE_EXISTS.getMessage());
        }

        // 创建角色
        SysRole role = new SysRole();
        role.setRoleCode(request.getRoleCode());
        role.setRoleName(request.getRoleName());
        role.setRoleDesc(request.getRoleDesc());
        role.setIsDeleted((byte) 0);
        this.save(role);

        // 分配权限
        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            assignPermissions(role.getId(), request.getPermissionIds());
        }

        return convertToResponse(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysRoleResponse updateRole(Long id, UpdateRoleRequest request) {
        SysRole role = this.getById(id);
        if (role == null || (role.getIsDeleted() != null && role.getIsDeleted() == 1)) {
            throw new ApiException(ResultCode.ROLE_EXISTS.getMessage());
        }

        // 检查角色编码是否与其他角色冲突
        if (StringUtils.hasText(request.getRoleCode()) && !request.getRoleCode().equals(role.getRoleCode())) {
            LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysRole::getRoleCode, request.getRoleCode())
                    .eq(SysRole::getIsDeleted, 0)
                    .ne(SysRole::getId, id);
            SysRole existRole = this.getOne(queryWrapper);
            if (existRole != null) {
                throw new ApiException(ResultCode.ROLE_CODE_EXISTS.getMessage());
            }
            role.setRoleCode(request.getRoleCode());
        }

        // 更新角色信息
        if (StringUtils.hasText(request.getRoleName())) {
            role.setRoleName(request.getRoleName());
        }
        if (StringUtils.hasText(request.getRoleDesc())) {
            role.setRoleDesc(request.getRoleDesc());
        }
        this.updateById(role);

        // 更新权限
        if (request.getPermissionIds() != null) {
            assignPermissions(id, request.getPermissionIds());
        }

        return convertToResponse(role);
    }

    @Override
    public void deleteRole(Long id) {
        SysRole role = this.getById(id);
        if (role == null || (role.getIsDeleted() != null && role.getIsDeleted() == 1)) {
            throw new ApiException(ResultCode.ROLE_NOT_FOUNT.getMessage());
        }
        role.setIsDeleted((byte) 1);
        this.updateById(role);
    }

    @Override
    public List<Long> getRolePermissionIds(Long roleId) {
        LambdaQueryWrapper<SysRolePermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRolePermission::getRoleId, roleId);
        List<SysRolePermission> rolePermissions = sysRolePermissionService.list(queryWrapper);
        return rolePermissions.stream()
                .map(SysRolePermission::getPermissionId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        // 查询该角色现有的权限ID列表
        LambdaQueryWrapper<SysRolePermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRolePermission::getRoleId, roleId);
        List<SysRolePermission> existingRolePermissions = sysRolePermissionService.list(queryWrapper);
        Set<Long> existingPermissionIds = existingRolePermissions.stream()
                .map(SysRolePermission::getPermissionId)
                .collect(Collectors.toSet());

        // 处理空列表情况，使用 HashSet 提高查找效率
        Set<Long> newPermissionIds = (permissionIds != null && !permissionIds.isEmpty()) 
                ? new HashSet<>(permissionIds) 
                : new HashSet<>();

        // 计算需要删除的权限（现有权限 - 新权限）
        List<Long> toDeleteIds = existingPermissionIds.stream()
                .filter(id -> !newPermissionIds.contains(id))
                .collect(Collectors.toList());

        // 计算需要新增的权限（新权限 - 现有权限）
        List<Long> toAddIds = newPermissionIds.stream()
                .filter(id -> !existingPermissionIds.contains(id))
                .collect(Collectors.toList());

        // 批量删除需要删除的权限
        if (!toDeleteIds.isEmpty()) {
            LambdaQueryWrapper<SysRolePermission> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(SysRolePermission::getRoleId, roleId)
                    .in(SysRolePermission::getPermissionId, toDeleteIds);
            sysRolePermissionService.remove(deleteWrapper);
        }

        // 批量添加需要新增的权限
        if (!toAddIds.isEmpty()) {
            List<SysRolePermission> rolePermissions = toAddIds.stream()
                    .map(permissionId -> {
                        SysRolePermission rolePermission = new SysRolePermission();
                        rolePermission.setRoleId(roleId);
                        rolePermission.setPermissionId(permissionId);
                        return rolePermission;
                    })
                    .collect(Collectors.toList());
            sysRolePermissionService.saveBatch(rolePermissions);
        }
    }

    @Override
    public SysRoleListResponse getRoleList(SysRoleListRequest request) {
        long current = request.getCurrent() != null && request.getCurrent() > 0 ? request.getCurrent() : 1;
        long size = request.getSize() != null && request.getSize() > 0 ? request.getSize() : 10;
        Page<SysRole> page = new Page<>(current, size);
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getIsDeleted, 0);

        if (StringUtils.hasText(request.getRoleCode())) {
            queryWrapper.like(SysRole::getRoleCode, request.getRoleCode());
        }
        if (StringUtils.hasText(request.getRoleName())) {
            queryWrapper.like(SysRole::getRoleName, request.getRoleName());
        }
        queryWrapper.orderByDesc(SysRole::getCreateTime);

        Page<SysRole> result = this.page(page, queryWrapper);

        SysRoleListResponse response = new SysRoleListResponse();
        response.setTotal(result.getTotal());
        response.setCurrent(current);
        response.setSize(size);

        List<SysRoleResponse> responseList = new ArrayList<>();
        for (SysRole role : result.getRecords()) {
            responseList.add(convertToResponse(role));
        }
        response.setRecords(responseList);

        return response;
    }

    @Override
    public List<SysRoleResponse> getAllRoles() {
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getIsDeleted, 0);
        List<SysRole> roles = this.list(queryWrapper);
        return roles.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SysRoleResponse getRoleById(Long id) {
        SysRole role = this.getById(id);
        if (role == null) {
            throw new ApiException(ResultCode.ROLE_NOT_FOUNT.getMessage());
        }
        return convertToResponse(role);
    }

    private SysRoleResponse convertToResponse(SysRole role) {
        SysRoleResponse response = new SysRoleResponse();
        BeanUtils.copyProperties(role, response);
        return response;
    }
}
