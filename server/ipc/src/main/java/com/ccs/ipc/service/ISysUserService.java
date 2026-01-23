package com.ccs.ipc.service;

import com.ccs.ipc.dto.*;
import com.ccs.ipc.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 系统用户表（统一存储患者/医生/管理员） 服务类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
public interface ISysUserService extends IService<SysUser> {

    /**
     * 登录
     */
    LoginResponse login(String username, String password);

    /**
     * 修改当前用户信息
     */
    SysUser updateUser(Long userId, UpdateUserRequest request);

    /**
     * 修改密码
     */
    void changePassword(Long userId, ChangePasswordRequest request);

    /**
     * 根据用户名获取用户ID
     */
    Long getUserIdByUsername(String username);

    /**
     * 创建用户（管理员功能）
     *
     * @param request 创建用户请求
     * @return 创建的用户
     */
    SysUser createUser(CreateUserRequest request);

    /**
     * 更新用户（管理员功能）
     *
     * @param id      用户ID
     * @param request 更新用户请求
     * @return 更新后的用户
     */
    SysUser updateUserByAdmin(Long id, UpdateUserRequest request);

    /**
     * 删除用户（逻辑删除）
     *
     * @param id 用户ID
     */
    void deleteUser(Long id);

    /**
     * 重置用户密码
     *
     * @param id      用户ID
     * @param request 重置密码请求
     */
    void resetPassword(Long id, ResetPasswordRequest request);

    /**
     * 获取用户的角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> getUserRoleIds(Long userId);

    /**
     * 获取用户的API权限编码列表（用于权限拦截器）
     *
     * @param userId 用户ID
     * @return API权限编码列表（类型为3）
     */
    List<String> getUserApiPermissions(Long userId);

    /**
     * 分页查询用户列表
     *
     * @param request 查询请求
     * @return 用户列表
     */
    SysUserListResponse getUserList(SysUserListRequest request);

    /**
     * 根据ID获取用户详情
     *
     * @param id 用户ID
     * @return 用户详情
     */
    SysUserResponse getUserById(Long id);
}
