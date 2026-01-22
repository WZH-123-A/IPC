package com.ccs.ipc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ccs.ipc.common.exception.ApiException;
import com.ccs.ipc.common.response.ResultCode;
import com.ccs.ipc.common.util.JwtUtil;
import com.ccs.ipc.common.util.PasswordUtil;
import com.ccs.ipc.dto.ChangePasswordRequest;
import com.ccs.ipc.dto.LoginResponse;
import com.ccs.ipc.dto.UpdateUserRequest;
import com.ccs.ipc.entity.SysRole;
import com.ccs.ipc.entity.SysUser;
import com.ccs.ipc.entity.SysUserRole;
import com.ccs.ipc.mapper.SysUserMapper;
import com.ccs.ipc.service.ISysRoleService;
import com.ccs.ipc.service.ISysUserRoleService;
import com.ccs.ipc.service.ISysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统用户表（统一存储患者/医生/管理员） 服务实现类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ISysUserRoleService sysUserRoleService;

    @Autowired
    private ISysRoleService sysRoleService;

    @Override
    public LoginResponse login(String username, String password) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, username)
                .eq(SysUser::getIsDeleted, 0);
        SysUser user = this.getOne(queryWrapper);

        if (user == null) {
            throw new ApiException(ResultCode.USER_NOT_FOUND.getMessage());
        }

        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new ApiException(ResultCode.USER_DISABLED.getMessage());
        }

        if (!PasswordUtil.matches(password, user.getPassword())) {
            throw new ApiException(ResultCode.USER_PASSWORD_ERROR.getMessage());
        }

        user.setLastLoginTime(LocalDateTime.now());
        this.updateById(user);

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // 查询用户角色列表
        List<String> roleCodes = getUserRoleCodes(user.getId());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setRoles(roleCodes);

        return response;
    }

    @Override
    public SysUser updateUser(Long userId, UpdateUserRequest request) {
        SysUser user = this.getById(userId);
        
        if (user == null) {
            throw new ApiException(ResultCode.USER_NOT_FOUND.getMessage());
        }

        if (user.getIsDeleted() != null && user.getIsDeleted() == 1) {
            throw new ApiException(ResultCode.USER_NOT_FOUND.getMessage());
        }

        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getRealName() != null) {
            user.setRealName(request.getRealName());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }

        this.updateById(user);
        return user;
    }

    @Override
    public void changePassword(Long userId, ChangePasswordRequest request) {
        SysUser user = this.getById(userId);
        
        if (user == null) {
            throw new ApiException(ResultCode.USER_NOT_FOUND.getMessage());
        }

        if (user.getIsDeleted() != null && user.getIsDeleted() == 1) {
            throw new ApiException(ResultCode.USER_NOT_FOUND.getMessage());
        }

        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new ApiException(ResultCode.USER_DISABLED.getMessage());
        }

        if (!PasswordUtil.matches(request.getOldPassword(), user.getPassword())) {
            throw new ApiException(ResultCode.USER_PASSWORD_ERROR.getMessage());
        }

        if (request.getOldPassword().equals(request.getNewPassword())) {
            throw new ApiException("新密码不能与旧密码相同");
        }

        String encodedNewPassword = PasswordUtil.encode(request.getNewPassword());
        user.setPassword(encodedNewPassword);
        this.updateById(user);
    }

    @Override
    public Long getUserIdByUsername(String username) {
        if (username == null || username.isEmpty()) {
            return null;
        }
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, username)
                .eq(SysUser::getIsDeleted, 0)
                .select(SysUser::getId);
        SysUser user = this.getOne(queryWrapper);
        return user != null ? user.getId() : null;
    }

    /**
     * 获取用户角色编码列表
     *
     * @param userId 用户ID
     * @return 角色编码列表，如果用户没有角色则返回空列表
     */
    private List<String> getUserRoleCodes(Long userId) {
        // 查询用户角色关联
        LambdaQueryWrapper<SysUserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> userRoles = sysUserRoleService.list(userRoleWrapper);

        if (userRoles == null || userRoles.isEmpty()) {
            return new java.util.ArrayList<>();
        }

        // 获取所有角色ID
        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());

        // 批量查询角色信息
        List<SysRole> roles = sysRoleService.listByIds(roleIds);

        // 提取角色编码
        return roles.stream()
                .map(SysRole::getRoleCode)
                .collect(Collectors.toList());
    }
}
