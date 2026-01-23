package com.ccs.ipc.service;

import com.ccs.ipc.dto.CreateRoleRequest;
import com.ccs.ipc.dto.UpdateRoleRequest;
import com.ccs.ipc.entity.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 系统角色表 服务类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
public interface ISysRoleService extends IService<SysRole> {

    /**
     * 创建角色
     *
     * @param request 创建角色请求
     * @return 创建的角色
     */
    SysRole createRole(CreateRoleRequest request);

    /**
     * 更新角色
     *
     * @param id      角色ID
     * @param request 更新角色请求
     * @return 更新后的角色
     */
    SysRole updateRole(Long id, UpdateRoleRequest request);

    /**
     * 删除角色（逻辑删除）
     *
     * @param id 角色ID
     */
    void deleteRole(Long id);

    /**
     * 获取角色的权限ID列表
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    List<Long> getRolePermissionIds(Long roleId);

    /**
     * 分配权限给角色
     *
     * @param roleId        角色ID
     * @param permissionIds 权限ID列表
     */
    void assignPermissions(Long roleId, List<Long> permissionIds);
}
