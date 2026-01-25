package com.ccs.ipc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ccs.ipc.common.exception.ApiException;
import com.ccs.ipc.common.response.ResultCode;
import com.ccs.ipc.common.util.JwtUtil;
import com.ccs.ipc.common.util.PasswordUtil;
import com.ccs.ipc.dto.auth.LoginResponse;
import com.ccs.ipc.dto.userdto.*;
import com.ccs.ipc.entity.*;
import com.ccs.ipc.mapper.SysUserMapper;
import com.ccs.ipc.service.ISysPermissionService;
import com.ccs.ipc.service.ISysRolePermissionService;
import com.ccs.ipc.service.ISysRoleService;
import com.ccs.ipc.service.ISysUserRoleService;
import com.ccs.ipc.service.ISysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Autowired
    private ISysRolePermissionService sysRolePermissionService;

    @Autowired
    private ISysPermissionService sysPermissionService;

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

        // 查询用户权限列表
        List<String> permissionCodes = getUserPermissionCodes(user.getId());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setRoles(roleCodes);
        response.setPermissions(permissionCodes);

        return response;
    }

    @Override
    public SysUserResponse updateUser(Long userId, UpdateUserRequest request) {
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
        return convertToResponse(user);
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
            throw new ApiException(ResultCode.USER_PASSWORD_NOT_DIFFERENT.getMessage());
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

    /**
     * 获取用户权限编码列表
     * 通过用户 -> 角色 -> 权限的关联关系查询
     *
     * @param userId 用户ID
     * @return 权限编码列表，如果用户没有权限则返回空列表
     */
    private List<String> getUserPermissionCodes(Long userId) {
        // 1. 查询用户角色关联
        LambdaQueryWrapper<SysUserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> userRoles = sysUserRoleService.list(userRoleWrapper);

        if (userRoles == null || userRoles.isEmpty()) {
            return new java.util.ArrayList<>();
        }

        // 2. 获取所有角色ID
        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());

        // 3. 查询角色权限关联
        LambdaQueryWrapper<SysRolePermission> rolePermissionWrapper = new LambdaQueryWrapper<>();
        rolePermissionWrapper.in(SysRolePermission::getRoleId, roleIds);
        List<SysRolePermission> rolePermissions = sysRolePermissionService.list(rolePermissionWrapper);

        if (rolePermissions == null || rolePermissions.isEmpty()) {
            return new java.util.ArrayList<>();
        }

        // 4. 获取所有权限ID
        List<Long> permissionIds = rolePermissions.stream()
                .map(SysRolePermission::getPermissionId)
                .distinct()
                .collect(Collectors.toList());

        // 5. 批量查询权限信息
        List<SysPermission> permissions = sysPermissionService.listByIds(permissionIds);

        // 6. 提取权限编码（返回菜单权限和路由权限，类型为1和4）
        return permissions.stream()
                .filter(p -> p.getPermissionType() != null && (p.getPermissionType() == 1 || p.getPermissionType() == 4))
                .map(SysPermission::getPermissionCode)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUserResponse createUser(CreateUserRequest request) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, request.getUsername())
                .eq(SysUser::getIsDeleted, 0);
        SysUser existUser = this.getOne(queryWrapper);
        if (existUser != null) {
            throw new ApiException(ResultCode.USER_ALREADY_EXISTS.getMessage());
        }

        // 创建用户
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordUtil.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setRealName(request.getRealName());
        user.setGender(request.getGender());
        user.setStatus(request.getStatus() != null ? request.getStatus() : (byte) 1);
        user.setIsDeleted((byte) 0);
        this.save(user);

        // 分配角色
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            assignRoles(user.getId(), request.getRoleIds());
        }

        return convertToResponse(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUserResponse updateUserByAdmin(Long id, UpdateUserRequest request) {
        SysUser user = this.getById(id);
        if (user == null || (user.getIsDeleted() != null && user.getIsDeleted() == 1)) {
            throw new ApiException(ResultCode.USER_NOT_FOUND.getMessage());
        }

        // 更新用户信息
        if (StringUtils.hasText(request.getPhone())) {
            user.setPhone(request.getPhone());
        }
        if (StringUtils.hasText(request.getEmail())) {
            user.setEmail(request.getEmail());
        }
        if (StringUtils.hasText(request.getRealName())) {
            user.setRealName(request.getRealName());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        this.updateById(user);

        // 更新角色
        if (request.getRoleIds() != null) {
            assignRoles(id, request.getRoleIds());
        }

        return convertToResponse(user);
    }

    @Override
    public void deleteUser(Long id) {
        SysUser user = this.getById(id);
        if (user == null || (user.getIsDeleted() != null && user.getIsDeleted() == 1)) {
            throw new ApiException(ResultCode.USER_NOT_FOUND.getMessage());
        }
        user.setIsDeleted((byte) 1);
        this.updateById(user);
    }

    @Override
    public void resetPassword(Long id, ResetPasswordRequest request) {
        SysUser user = this.getById(id);
        if (user == null || (user.getIsDeleted() != null && user.getIsDeleted() == 1)) {
            throw new ApiException(ResultCode.USER_NOT_FOUND.getMessage());
        }
        user.setPassword(PasswordUtil.encode(request.getPassword()));
        this.updateById(user);
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> userRoles = sysUserRoleService.list(queryWrapper);
        return userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getUserApiPermissions(Long userId) {
        // 1. 查询用户角色关联
        LambdaQueryWrapper<SysUserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> userRoles = sysUserRoleService.list(userRoleWrapper);

        if (userRoles == null || userRoles.isEmpty()) {
            return new java.util.ArrayList<>();
        }

        // 2. 获取所有角色ID
        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());

        // 3. 查询角色权限关联
        LambdaQueryWrapper<SysRolePermission> rolePermissionWrapper = new LambdaQueryWrapper<>();
        rolePermissionWrapper.in(SysRolePermission::getRoleId, roleIds);
        List<SysRolePermission> rolePermissions = sysRolePermissionService.list(rolePermissionWrapper);

        if (rolePermissions == null || rolePermissions.isEmpty()) {
            return new java.util.ArrayList<>();
        }

        // 4. 获取所有权限ID
        List<Long> permissionIds = rolePermissions.stream()
                .map(SysRolePermission::getPermissionId)
                .distinct()
                .collect(Collectors.toList());

        // 5. 批量查询权限信息
        List<SysPermission> permissions = sysPermissionService.listByIds(permissionIds);

        // 6. 提取API权限编码（类型为3）
        return permissions.stream()
                .filter(p -> p.getPermissionType() != null && p.getPermissionType() == 3)
                .map(SysPermission::getPermissionCode)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public SysUserListResponse getUserList(SysUserListRequest request) {
        long current = request.getCurrent() != null && request.getCurrent() > 0 ? request.getCurrent() : 1;
        long size = request.getSize() != null && request.getSize() > 0 ? request.getSize() : 10;

        Page<SysUser> page = new Page<>(current, size);
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getIsDeleted, 0);

        if (StringUtils.hasText(request.getUserName())) {
            queryWrapper.like(SysUser::getUsername, request.getUserName());
        }
        if (StringUtils.hasText(request.getRealName())) {
            queryWrapper.like(SysUser::getRealName, request.getRealName());
        }
        if (request.getStatus() != null) {
            queryWrapper.eq(SysUser::getStatus, request.getStatus());
        }
        queryWrapper.orderByDesc(SysUser::getCreateTime);

        Page<SysUser> result = this.page(page, queryWrapper);

        SysUserListResponse response = new SysUserListResponse();
        response.setTotal(result.getTotal());
        response.setCurrent(result.getCurrent());
        response.setSize(result.getSize());

        List<SysUserResponse> responseList = new ArrayList<>();
        for(SysUser user : result.getRecords()){
            responseList.add(convertToResponse(user));
        }
        response.setRecords(responseList);
        return response;
    }

    @Override
    public SysUserResponse getUserById(Long id)
    {
        SysUser user = this.getById(id);
        if (user == null)
        {
            throw new ApiException(ResultCode.USER_NOT_FOUND.getMessage());
        }
        return convertToResponse(user);
    }

    /**
     * 分配角色给用户
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     */
    private void assignRoles(Long userId, List<Long> roleIds) {
        // 删除原有角色
        LambdaQueryWrapper<SysUserRole> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(SysUserRole::getUserId, userId);
        sysUserRoleService.remove(deleteWrapper);

        // 添加新角色
        if (roleIds != null && !roleIds.isEmpty()) {
            List<SysUserRole> userRoles = roleIds.stream()
                    .map(roleId -> {
                        SysUserRole userRole = new SysUserRole();
                        userRole.setUserId(userId);
                        userRole.setRoleId(roleId);
                        return userRole;
                    })
                    .collect(Collectors.toList());
            sysUserRoleService.saveBatch(userRoles);
        }
    }

    /**
     * 将实体转换为Response
     */
    private SysUserResponse convertToResponse(SysUser user) {
        SysUserResponse response = new SysUserResponse();
        BeanUtils.copyProperties(user, response);
        return response;
    }
}
