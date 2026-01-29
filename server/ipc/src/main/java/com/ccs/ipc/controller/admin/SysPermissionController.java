package com.ccs.ipc.controller.admin;

import com.ccs.ipc.common.annotation.Log;
import com.ccs.ipc.common.annotation.RequirePermission;
import com.ccs.ipc.common.enums.log.PermissionModule;
import com.ccs.ipc.common.enums.log.PermissionOperation;
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
@RequestMapping("/api/admin/permission")
public class SysPermissionController {

    @Autowired
    private ISysPermissionService sysPermissionService;

    /**
     * 获取所有权限（树形结构）
     */
    @GetMapping("/tree")
    @RequirePermission("admin:api:permission:list")
    @Log(operationType = PermissionOperation.C.QUERY_TREE, operationModule = PermissionModule.C.PERMISSION, operationDesc = "获取所有权限（树形结构）")
    public Response<List<PermissionTreeNode>> getPermissionTree(
            @RequestParam(required = false) Byte permissionType) {
        List<PermissionTreeNode> tree = sysPermissionService.getPermissionTree(permissionType);
        return Response.success(tree);
    }

    /**
     * 获取所有权限（列表）
     */
    @GetMapping("/list")
    @RequirePermission("admin:api:permission:list")
    @Log(operationType = PermissionOperation.C.QUERY_LIST, operationModule = PermissionModule.C.PERMISSION, operationDesc = "获取所有权限（列表）")
    public Response<List<SysPermissionResponse>> getPermissionList(
            @RequestParam(required = false) Byte permissionType) {
        List<SysPermissionResponse> responses = sysPermissionService.getPermissionList(permissionType);
        return Response.success(responses);
    }

    /**
     * 根据ID获取权限详情
     */
    @GetMapping("/{id}")
    @RequirePermission("admin:api:permission:detail")
    @Log(operationType = PermissionOperation.C.QUERY_DETAIL, operationModule = PermissionModule.C.PERMISSION, operationDesc = "根据ID获取权限详情")
    public Response<SysPermissionResponse> getPermissionById(@PathVariable Long id) {
        SysPermissionResponse response = sysPermissionService.getPermissionById(id);
        return Response.success(response);
    }

    /**
     * 新增权限
     */
    @PostMapping
    @RequirePermission("admin:api:permission:create")
    @Log(operationType = PermissionOperation.C.ADD, operationModule = PermissionModule.C.PERMISSION, operationDesc = "新增权限")
    public Response<SysPermissionResponse> createPermission(@Valid @RequestBody CreatePermissionRequest request) {
        SysPermissionResponse response = sysPermissionService.createPermission(request);
        return Response.success(response);
    }

    /**
     * 更新权限
     */
    @PutMapping("/{id}")
    @RequirePermission("admin:api:permission:update")
    @Log(operationType = PermissionOperation.C.UPDATE, operationModule = PermissionModule.C.PERMISSION, operationDesc = "更新权限")
    public Response<SysPermissionResponse> updatePermission(@PathVariable Long id, @RequestBody UpdatePermissionRequest request) {
        SysPermissionResponse response = sysPermissionService.updatePermission(id, request);
        return Response.success(response);
    }

    /**
     * 删除权限（逻辑删除）
     */
    @DeleteMapping("/{id}")
    @RequirePermission("admin:api:permission:delete")
    @Log(operationType = PermissionOperation.C.DELETE, operationModule = PermissionModule.C.PERMISSION, operationDesc = "删除权限")
    public Response<Void> deletePermission(@PathVariable Long id) {
        sysPermissionService.deletePermission(id);
        return Response.success();
    }
}
