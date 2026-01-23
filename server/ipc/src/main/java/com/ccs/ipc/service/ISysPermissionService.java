package com.ccs.ipc.service;

import com.ccs.ipc.dto.CreatePermissionRequest;
import com.ccs.ipc.dto.PermissionTreeNode;
import com.ccs.ipc.dto.UpdatePermissionRequest;
import com.ccs.ipc.entity.SysPermission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 系统权限表 服务类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
public interface ISysPermissionService extends IService<SysPermission> {

    /**
     * 获取权限树
     *
     * @param permissionType 权限类型（可选）
     * @return 权限树
     */
    List<PermissionTreeNode> getPermissionTree(Byte permissionType);

    /**
     * 创建权限
     *
     * @param request 创建权限请求
     * @return 创建的权限
     */
    SysPermission createPermission(CreatePermissionRequest request);

    /**
     * 更新权限
     *
     * @param id      权限ID
     * @param request 更新权限请求
     * @return 更新后的权限
     */
    SysPermission updatePermission(Long id, UpdatePermissionRequest request);

    /**
     * 删除权限（逻辑删除）
     *
     * @param id 权限ID
     */
    void deletePermission(Long id);
}
