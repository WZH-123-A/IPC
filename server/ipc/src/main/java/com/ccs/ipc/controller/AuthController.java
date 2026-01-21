package com.ccs.ipc.controller;

import com.ccs.ipc.common.annotation.Log;
import com.ccs.ipc.common.enums.OperationModule;
import com.ccs.ipc.common.enums.OperationType;
import com.ccs.ipc.common.response.Response;
import com.ccs.ipc.dto.LoginRequest;
import com.ccs.ipc.dto.LoginResponse;
import com.ccs.ipc.service.ISysUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @Log(operationType = OperationType.LOGIN, operationModule = OperationModule.AUTH, operationDesc = "用户登录", saveRequestData = true, saveResponseData = true)
    public Response<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = sysUserService.login(request.getUsername(), request.getPassword());
        return Response.success(response);
    }
}

