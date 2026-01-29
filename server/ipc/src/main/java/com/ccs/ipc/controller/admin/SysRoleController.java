package com.ccs.ipc.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ccs.ipc.common.annotation.Log;
import com.ccs.ipc.common.annotation.RequirePermission;
import com.ccs.ipc.common.enums.OperationModule;
import com.ccs.ipc.common.enums.OperationType;
import com.ccs.ipc.common.response.Response;
import com.ccs.ipc.dto.roledto.*;
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
@RequestMapping("/api/admin/role")
public class SysRoleController {

    @Autowired
    private ISysRoleService sysRoleService;

    /**
     * 分页查询角色列表
     */
    @GetMapping("/list")
    @RequirePermission("admin:api:role:list")
    @Log(operationType = OperationType.QUERY, operationModule = OperationModule.ROLE, operationDesc = "分页查询角色列表")
    public Response<SysRoleListResponse> getRoleList(SysRoleListRequest request) {
        SysRoleListResponse response = sysRoleService.getRoleList(request);
        return Response.success(response);
    }

    /**
     * 获取所有角色（不分页）
     */
    @GetMapping("/all")
    @RequirePermission("admin:api:role:list")
    @Log(operationType = OperationType.QUERY, operationModule = OperationModule.ROLE, operationDesc = "获取所有角色（不分页）")
    public Response<List<SysRoleResponse>> getAllRoles() {
        List<SysRoleResponse> responses = sysRoleService.getAllRoles();
        return Response.success(responses);
    }

    /**
     * 根据ID获取角色详情
     */
    @GetMapping("/{id}")
    @RequirePermission("admin:api:role:detail")
    @Log(operationType = OperationType.QUERY, operationModule = OperationModule.ROLE, operationDesc = "根据ID获取角色详情")
    public Response<SysRoleResponse> getRoleById(@PathVariable Long id) {
        SysRoleResponse response = sysRoleService.getRoleById(id);
        return Response.success(response);
    }

    /**
     * 新增角色
     */
    @PostMapping
    @RequirePermission("admin:api:role:create")
    @Log(operationType = OperationType.ADD, operationModule = OperationModule.ROLE, operationDesc = "新增角色")
    public Response<SysRoleResponse> createRole(@Valid @RequestBody CreateRoleRequest request) {
        SysRoleResponse response = sysRoleService.createRole(request);
        return Response.success(response);
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    @RequirePermission("admin:api:role:update")
    @Log(operationType = OperationType.UPDATE, operationModule = OperationModule.ROLE, operationDesc = "更新角色")
    public Response<SysRoleResponse> updateRole(@PathVariable Long id, @Valid @RequestBody UpdateRoleRequest request) {
        SysRoleResponse response = sysRoleService.updateRole(id, request);
        return Response.success(response);
    }

    /**
     * 删除角色（逻辑删除）
     */
    @DeleteMapping("/{id}")
    @RequirePermission("admin:api:role:delete")
    @Log(operationType = OperationType.DELETE, operationModule = OperationModule.ROLE, operationDesc = "删除角色")
    public Response<Void> deleteRole(@PathVariable Long id) {
        sysRoleService.deleteRole(id);
        return Response.success();
    }

    /**
     * 获取角色的权限ID列表
     */
    @GetMapping("/{id}/permissions")
    @RequirePermission("admin:api:role:detail")
    @Log(operationType = OperationType.QUERY, operationModule = OperationModule.ROLE, operationDesc = "获取角色的权限ID列表")
    public Response<List<Long>> getRolePermissions(@PathVariable Long id) {
        List<Long> permissionIds = sysRoleService.getRolePermissionIds(id);
        return Response.success(permissionIds);
    }
}
