package com.ccs.ipc.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ccs.ipc.common.annotation.RequirePermission;
import com.ccs.ipc.common.response.Response;
import com.ccs.ipc.dto.permissiondto.CreatePermissionRequest;
import com.ccs.ipc.dto.permissiondto.PermissionTreeNode;
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
    public Response<List<SysPermission>> getPermissionList(
            @RequestParam(required = false) Byte permissionType) {
        LambdaQueryWrapper<SysPermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysPermission::getIsDeleted, 0);
        if (permissionType != null) {
            queryWrapper.eq(SysPermission::getPermissionType, permissionType);
        }
        queryWrapper.orderByAsc(SysPermission::getSort);
        
        List<SysPermission> permissions = sysPermissionService.list(queryWrapper);
        return Response.success(permissions);
    }

    /**
     * 根据ID获取权限详情
     */
    @GetMapping("/{id}")
    @RequirePermission("api:permission:detail")
    public Response<SysPermission> getPermissionById(@PathVariable Long id) {
        SysPermission permission = sysPermissionService.getById(id);
        if (permission == null || (permission.getIsDeleted() != null && permission.getIsDeleted() == 1)) {
            return Response.fail("权限不存在");
        }
        return Response.success(permission);
    }

    /**
     * 新增权限
     */
    @PostMapping
    @RequirePermission("api:permission:create")
    public Response<SysPermission> createPermission(@Valid @RequestBody CreatePermissionRequest request) {
        SysPermission permission = sysPermissionService.createPermission(request);
        return Response.success(permission);
    }

    /**
     * 更新权限
     */
    @PutMapping("/{id}")
    @RequirePermission("api:permission:update")
    public Response<SysPermission> updatePermission(@PathVariable Long id, @RequestBody UpdatePermissionRequest request) {
        SysPermission permission = sysPermissionService.updatePermission(id, request);
        return Response.success(permission);
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

