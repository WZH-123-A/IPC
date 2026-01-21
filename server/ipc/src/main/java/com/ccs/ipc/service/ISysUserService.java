package com.ccs.ipc.service;

import com.ccs.ipc.dto.ChangePasswordRequest;
import com.ccs.ipc.dto.LoginResponse;
import com.ccs.ipc.dto.UpdateUserRequest;
import com.ccs.ipc.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 系统用户表（统一存储患者/医生/管理员） 服务类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
public interface ISysUserService extends IService<SysUser> {

    //登录
    LoginResponse login(String username, String password);

    //修改信息
    SysUser updateUser(Long userId, UpdateUserRequest request);

    //修改密码
    void changePassword(Long userId, ChangePasswordRequest request);

}
