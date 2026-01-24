package com.ccs.ipc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ccs.ipc.common.exception.ApiException;
import com.ccs.ipc.common.response.ResultCode;
import com.ccs.ipc.dto.roledto.CreateRoleRequest;
import com.ccs.ipc.dto.roledto.UpdateRoleRequest;
import com.ccs.ipc.entity.SysRole;
import com.ccs.ipc.entity.SysRolePermission;
import com.ccs.ipc.mapper.SysRoleMapper;
import com.ccs.ipc.service.ISysRolePermissionService;
import com.ccs.ipc.service.ISysRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
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
    public SysRole createRole(CreateRoleRequest request) {
        // 检查角色编码是否已存在
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getRoleCode, request.getRoleCode())
                .eq(SysRole::getIsDeleted, 0);
        SysRole existRole = this.getOne(queryWrapper);
        if (existRole != null) {
            throw new ApiException(ResultCode.FAIL.getMessage() + ": 角色编码已存在");
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

        return role;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysRole updateRole(Long id, UpdateRoleRequest request) {
        SysRole role = this.getById(id);
        if (role == null || (role.getIsDeleted() != null && role.getIsDeleted() == 1)) {
            throw new ApiException(ResultCode.FAIL.getMessage() + ": 角色不存在");
        }

        // 检查角色编码是否与其他角色冲突
        if (StringUtils.hasText(request.getRoleCode()) && !request.getRoleCode().equals(role.getRoleCode())) {
            LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysRole::getRoleCode, request.getRoleCode())
                    .eq(SysRole::getIsDeleted, 0)
                    .ne(SysRole::getId, id);
            SysRole existRole = this.getOne(queryWrapper);
            if (existRole != null) {
                throw new ApiException(ResultCode.FAIL.getMessage() + ": 角色编码已存在");
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

        return role;
    }

    @Override
    public void deleteRole(Long id) {
        SysRole role = this.getById(id);
        if (role == null || (role.getIsDeleted() != null && role.getIsDeleted() == 1)) {
            throw new ApiException(ResultCode.FAIL.getMessage() + ": 角色不存在");
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
        // 删除原有权限
        LambdaQueryWrapper<SysRolePermission> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(SysRolePermission::getRoleId, roleId);
        sysRolePermissionService.remove(deleteWrapper);

        // 添加新权限
        if (permissionIds != null && !permissionIds.isEmpty()) {
            List<SysRolePermission> rolePermissions = permissionIds.stream()
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
}
