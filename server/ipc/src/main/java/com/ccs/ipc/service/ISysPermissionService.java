package com.ccs.ipc.service;

import com.ccs.ipc.dto.permissiondto.CreatePermissionRequest;
import com.ccs.ipc.dto.permissiondto.PermissionTreeNode;
import com.ccs.ipc.dto.permissiondto.SysPermissionResponse;
import com.ccs.ipc.dto.permissiondto.UpdatePermissionRequest;
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
    SysPermissionResponse createPermission(CreatePermissionRequest request);

    /**
     * 更新权限
     *
     * @param id      权限ID
     * @param request 更新权限请求
     * @return 更新后的权限
     */
    SysPermissionResponse updatePermission(Long id, UpdatePermissionRequest request);

    /**
     * 删除权限（逻辑删除）
     *
     * @param id 权限ID
     */
    void deletePermission(Long id);

    /**
     * 获取权限列表
     *
     * @param permissionType 权限类型（可选）
     * @return 权限列表
     */
    List<SysPermissionResponse> getPermissionList(Byte permissionType);

    /**
     * 根据ID获取权限详情
     *
     * @param id 权限ID
     * @return 权限详情
     */
    SysPermissionResponse getPermissionById(Long id);

    /**
     * 获取用户拥有的菜单权限树
     *
     * @param userId 用户ID
     * @return 菜单权限树
     */
    List<PermissionTreeNode> getUserMenuTree(Long userId);

    /**
     * 获取用户拥有的按钮权限编码列表
     *
     * @param userId 用户ID
     * @return 按钮权限编码列表
     */
    List<String> getUserButtonPermissions(Long userId);
}
