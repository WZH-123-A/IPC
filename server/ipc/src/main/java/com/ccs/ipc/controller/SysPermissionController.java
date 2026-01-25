package com.ccs.ipc.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ccs.ipc.common.annotation.Log;
import com.ccs.ipc.common.annotation.RequirePermission;
import com.ccs.ipc.common.enums.OperationModule;
import com.ccs.ipc.common.enums.OperationType;
import com.ccs.ipc.common.response.Response;
import com.ccs.ipc.dto.permissiondto.CreatePermissionRequest;
import com.ccs.ipc.dto.permissiondto.PermissionTreeNode;
import com.ccs.ipc.dto.permissiondto.SysPermissionResponse;
import com.ccs.ipc.dto.permissiondto.UpdatePermissionRequest;
import com.ccs.ipc.entity.SysPermission;
import com.ccs.ipc.service.ISysPermissionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统权限管理控制器
 * 统一管理权限相关接口，通过权限注解控制访问
 *
 * @author WZH
 * @since 2026-01-19
 */
@RestController
@RequestMapping("/api/permission")
public class SysPermissionController {

    @Autowired
    private ISysPermissionService sysPermissionService;

    /**
     * 获取所有权限（树形结构）
     */
    @GetMapping("/tree")
    @RequirePermission("api:permission:list")
    @Log(operationType = OperationType.QUERY, operationModule = OperationModule.PERMISSION, operationDesc = "获取所有权限（树形结构）")
    public Response<List<PermissionTreeNode>> getPermissionTree(
            @RequestParam(required = false) Byte permissionType) {
        List<PermissionTreeNode> tree = sysPermissionService.getPermissionTree(permissionType);
        return Response.success(tree);
    }

    /**
     * 获取所有权限（列表）
     */
    @GetMapping("/list")
    @RequirePermission("api:permission:list")
    @Log(operationType = OperationType.QUERY, operationModule = OperationModule.PERMISSION, operationDesc = "获取所有权限（列表）")
    public Response<List<SysPermissionResponse>> getPermissionList(
            @RequestParam(required = false) Byte permissionType) {
        List<SysPermissionResponse> responses = sysPermissionService.getPermissionList(permissionType);
        return Response.success(responses);
    }

    /**
     * 根据ID获取权限详情
     */
    @GetMapping("/{id}")
    @RequirePermission("api:permission:detail")
    @Log(operationType = OperationType.QUERY, operationModule = OperationModule.PERMISSION, operationDesc = "根据ID获取权限详情")
    public Response<SysPermissionResponse> getPermissionById(@PathVariable Long id) {
        SysPermissionResponse response = sysPermissionService.getPermissionById(id);
        return Response.success(response);
    }

    /**
     * 新增权限
     */
    @PostMapping
    @RequirePermission("api:permission:create")
    @Log(operationType = OperationType.ADD, operationModule = OperationModule.PERMISSION, operationDesc = "新增权限")
    public Response<SysPermissionResponse> createPermission(@Valid @RequestBody CreatePermissionRequest request) {
        SysPermissionResponse response = sysPermissionService.createPermission(request);
        return Response.success(response);
    }

    /**
     * 更新权限
     */
    @PutMapping("/{id}")
    @RequirePermission("api:permission:update")
    @Log(operationType = OperationType.UPDATE, operationModule = OperationModule.PERMISSION, operationDesc = "更新权限")
    public Response<SysPermissionResponse> updatePermission(@PathVariable Long id, @RequestBody UpdatePermissionRequest request) {
        SysPermissionResponse response = sysPermissionService.updatePermission(id, request);
        return Response.success(response);
    }

    /**
     * 删除权限（逻辑删除）
     */
    @DeleteMapping("/{id}")
    @RequirePermission("api:permission:delete")
    public Response<Void> deletePermission(@PathVariable Long id) {
        sysPermissionService.deletePermission(id);
        return Response.success();
    }
}

