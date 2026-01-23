package com.ccs.ipc.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ccs.ipc.common.annotation.Log;
import com.ccs.ipc.common.annotation.RequirePermission;
import com.ccs.ipc.common.enums.OperationModule;
import com.ccs.ipc.common.enums.OperationType;
import com.ccs.ipc.common.response.Response;
import com.ccs.ipc.dto.CreateRoleRequest;
import com.ccs.ipc.dto.UpdateRoleRequest;
import com.ccs.ipc.entity.SysRole;
import com.ccs.ipc.service.ISysRoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统角色管理控制器
 * 统一管理角色相关接口，通过权限注解控制访问
 *
 * @author WZH
 * @since 2026-01-19
 */
@RestController
@RequestMapping("/api/role")
public class SysRoleController {

    @Autowired
    private ISysRoleService sysRoleService;

    /**
     * 分页查询角色列表
     */
    @GetMapping("/list")
    @RequirePermission("api:role:list")
    public Response<Page<SysRole>> getRoleList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String roleCode,
            @RequestParam(required = false) String roleName) {
        Page<SysRole> page = new Page<>(current, size);
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getIsDeleted, 0);
        
        if (StringUtils.hasText(roleCode)) {
            queryWrapper.like(SysRole::getRoleCode, roleCode);
        }
        if (StringUtils.hasText(roleName)) {
            queryWrapper.like(SysRole::getRoleName, roleName);
        }
        queryWrapper.orderByDesc(SysRole::getCreateTime);
        
        Page<SysRole> result = sysRoleService.page(page, queryWrapper);
        return Response.success(result);
    }

    /**
     * 获取所有角色（不分页）
     */
    @GetMapping("/all")
    @RequirePermission("api:role:list")
    public Response<List<SysRole>> getAllRoles() {
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getIsDeleted, 0);
        queryWrapper.orderByAsc(SysRole::getRoleCode);
        List<SysRole> roles = sysRoleService.list(queryWrapper);
        return Response.success(roles);
    }

    /**
     * 根据ID获取角色详情
     */
    @GetMapping("/{id}")
    @RequirePermission("api:role:detail")
    public Response<SysRole> getRoleById(@PathVariable Long id) {
        SysRole role = sysRoleService.getById(id);
        if (role == null || (role.getIsDeleted() != null && role.getIsDeleted() == 1)) {
            return Response.fail("角色不存在");
        }
        return Response.success(role);
    }

    /**
     * 新增角色
     */
    @PostMapping
    @RequirePermission("api:role:create")
    @Log(operationType = OperationType.ADD, operationModule = OperationModule.USER, operationDesc = "新增角色")
    public Response<SysRole> createRole(@Valid @RequestBody CreateRoleRequest request) {
        SysRole role = sysRoleService.createRole(request);
        return Response.success(role);
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    @RequirePermission("api:role:update")
    @Log(operationType = OperationType.UPDATE, operationModule = OperationModule.USER, operationDesc = "更新角色")
    public Response<SysRole> updateRole(@PathVariable Long id, @Valid @RequestBody UpdateRoleRequest request) {
        SysRole role = sysRoleService.updateRole(id, request);
        return Response.success(role);
    }

    /**
     * 删除角色（逻辑删除）
     */
    @DeleteMapping("/{id}")
    @RequirePermission("api:role:delete")
    @Log(operationType = OperationType.DELETE, operationModule = OperationModule.USER, operationDesc = "删除角色")
    public Response<Void> deleteRole(@PathVariable Long id) {
        sysRoleService.deleteRole(id);
        return Response.success();
    }

    /**
     * 获取角色的权限ID列表
     */
    @GetMapping("/{id}/permissions")
    @RequirePermission("api:role:detail")
    public Response<List<Long>> getRolePermissions(@PathVariable Long id) {
        List<Long> permissionIds = sysRoleService.getRolePermissionIds(id);
        return Response.success(permissionIds);
    }
}

