package com.ccs.ipc.controller.admin;

import com.ccs.ipc.common.annotation.Log;
import com.ccs.ipc.common.annotation.RequirePermission;
import com.ccs.ipc.common.enums.OperationModule;
import com.ccs.ipc.common.enums.OperationType;
import com.ccs.ipc.common.response.Response;
import com.ccs.ipc.common.util.UserContext;
import com.ccs.ipc.dto.userdto.*;
import com.ccs.ipc.entity.SysUser;
import com.ccs.ipc.service.ISysUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 * 统一管理用户相关接口，通过权限注解控制访问
 *
 * @author WZH
 * @since 2026-01-19
 */
@RestController
@RequestMapping("/api/admin/user")
public class SysUserController {

    @Autowired
    private ISysUserService sysUserService;

    /**
     * 分页查询用户列表
     */
    @GetMapping("/list")
    @RequirePermission("admin:api:user:list")
    public Response<SysUserListResponse> getUserList(SysUserListRequest request) {
        SysUserListResponse response = sysUserService.getUserList(request);
        return Response.success(response);
    }

    /**
     * 根据ID获取用户详情
     */
    @GetMapping("/{id}")
    @RequirePermission("admin:api:user:detail")
    public Response<SysUserResponse> getUserById(@PathVariable Long id) {
        SysUserResponse response = sysUserService.getUserById(id);
        return Response.success(response);
    }

    /**
     * 新增用户
     */
    @PostMapping
    @RequirePermission("admin:api:user:create")
    @Log(operationType = OperationType.ADD, operationModule = OperationModule.USER, operationDesc = "新增用户")
    public Response<SysUserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        SysUserResponse response = sysUserService.createUser(request);
        return Response.success(response);
    }

    /**
     * 更新用户（管理员功能，可更新所有字段包括status和roleIds）
     */
    @PutMapping("/{id}")
    @RequirePermission("admin:api:user:update")
    @Log(operationType = OperationType.UPDATE, operationModule = OperationModule.USER, operationDesc = "更新用户")
    public Response<SysUserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        SysUserResponse response = sysUserService.updateUserByAdmin(id, request);
        return Response.success(response);
    }

    /**
     * 更新当前登录用户信息（用户自己更新，不能更新status和roleIds）
     */
    @PutMapping("/update")
    @Log(operationType = OperationType.UPDATE, operationModule = OperationModule.USER, operationDesc = "更新用户信息")
    public Response<SysUserResponse> updateCurrentUser(@Valid @RequestBody UpdateUserRequest request,
                                                HttpServletRequest httpRequest) {
        Long userId = UserContext.getUserId(httpRequest);
        SysUserResponse response = sysUserService.updateUser(userId, request);
        return Response.success(response);
    }

    /**
     * 删除用户（逻辑删除）
     */
    @DeleteMapping("/{id}")
    @RequirePermission("admin:api:user:delete")
    @Log(operationType = OperationType.DELETE, operationModule = OperationModule.USER, operationDesc = "删除用户")
    public Response<Void> deleteUser(@PathVariable Long id) {
        sysUserService.deleteUser(id);
        return Response.success();
    }

    /**
     * 重置用户密码
     */
    @PutMapping("/{id}/reset-password")
    @RequirePermission("admin:api:user:update")
    @Log(operationType = OperationType.UPDATE_PASSWORD, operationModule = OperationModule.USER, operationDesc = "重置用户密码")
    public Response<Void> resetPassword(@PathVariable Long id, @Valid @RequestBody ResetPasswordRequest request) {
        sysUserService.resetPassword(id, request);
        return Response.success();
    }

    /**
     * 修改密码（当前登录用户）
     */
    @PutMapping("/change-password")
    @Log(operationType = OperationType.UPDATE_PASSWORD, operationModule = OperationModule.USER, operationDesc = "修改密码", saveRequestData = true, saveResponseData = true)
    public Response<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                          HttpServletRequest httpRequest) {
        Long userId = UserContext.getUserId(httpRequest);
        sysUserService.changePassword(userId, request);
        return Response.success();
    }

    /**
     * 获取用户的角色ID列表
     */
    @GetMapping("/{id}/roles")
    @RequirePermission("admin:api:user:detail")
    public Response<List<Long>> getUserRoles(@PathVariable Long id) {
        List<Long> roleIds = sysUserService.getUserRoleIds(id);
        return Response.success(roleIds);
    }
}
