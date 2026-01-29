package com.ccs.ipc.controller;

import com.ccs.ipc.common.annotation.Log;
import com.ccs.ipc.common.enums.log.AuthModule;
import com.ccs.ipc.common.enums.log.AuthOperation;
import com.ccs.ipc.common.response.Response;
import com.ccs.ipc.common.util.UserContext;
import com.ccs.ipc.dto.auth.LoginRequest;
import com.ccs.ipc.dto.auth.LoginResponse;
import com.ccs.ipc.dto.permissiondto.PermissionTreeNode;
import com.ccs.ipc.service.ISysPermissionService;
import com.ccs.ipc.service.ISysUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 认证控制器
 *
 * @author WZH
 * @since 2026-01-19
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ISysPermissionService sysPermissionService;

    /**
     * 用户登录
     *
     * @param request 登录请求信息
     * {
     *      "username": "用户名",
     *      "password": "密码"
     * }
     * @return 登录响应信息
     */
    @PostMapping("/login")
    @Log(operationType = AuthOperation.C.LOGIN, operationModule = AuthModule.C.AUTH, operationDesc = "用户登录", saveRequestData = true, saveResponseData = true)
    public Response<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = sysUserService.login(request.getUsername(), request.getPassword());
        return Response.success(response);
    }

    /**
     * 获取当前用户的权限树
     *
     * @param request HTTP请求
     * @return 权限树
     */
    @GetMapping("/permissions")
    @Log(operationType = AuthOperation.C.GET_PERMISSION_TREE, operationModule = AuthModule.C.AUTH, operationDesc = "获取当前用户的权限树", saveRequestData = true, saveResponseData = true)
    public Response<List<PermissionTreeNode>> getCurrentUserPermissions(HttpServletRequest request) {
        Long userId = UserContext.getUserId(request);
        List<PermissionTreeNode> permissionTree = sysPermissionService.getUserPermissionTree(userId);
        return Response.success(permissionTree);
    }

    /**
     * 获取当前用户的菜单权限树
     *
     * @param request HTTP请求
     * @return 菜单权限树
     */
    @GetMapping("/menus")
    @Log(operationType = AuthOperation.C.GET_MENUS, operationModule = AuthModule.C.AUTH, operationDesc = "获取当前用户的菜单权限树")
    public Response<List<PermissionTreeNode>> getCurrentUserMenus(HttpServletRequest request) {
        Long userId = UserContext.getUserId(request);
        List<PermissionTreeNode> menuTree = sysPermissionService.getUserMenuTree(userId);
        return Response.success(menuTree);
    }

    /**
     * 获取当前用户的按钮权限列表
     *
     * @param request HTTP请求
     * @return 按钮权限编码列表
     */
    @GetMapping("/buttons")
    @Log(operationType = AuthOperation.C.GET_BUTTONS, operationModule = AuthModule.C.AUTH, operationDesc = "获取当前用户的按钮权限列表")
    public Response<List<String>> getCurrentUserButtons(HttpServletRequest request) {
        Long userId = UserContext.getUserId(request);
        List<String> buttonPermissions = sysPermissionService.getUserButtonPermissions(userId);
        return Response.success(buttonPermissions);
    }
}

