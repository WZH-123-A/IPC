package com.ccs.ipc.controller;

import com.ccs.ipc.common.response.Response;
import com.ccs.ipc.common.util.UserContext;
import com.ccs.ipc.dto.ChangePasswordRequest;
import com.ccs.ipc.dto.UpdateUserRequest;
import com.ccs.ipc.entity.SysUser;
import com.ccs.ipc.service.ISysUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 *
 * @author WZH
 * @since 2026-01-19
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private ISysUserService sysUserService;

    /**
     * 更新当前登录用户信息
     *
     * @param request 更新请求信息
     * {
     *     "phone": "手机号",
     *     "email": "邮箱",
     *     "realName": "真实姓名",
     *     "gender": 1,  // 0-未知 1-男 2-女
     *     "avatar": "头像URL"
     * }
     * @param httpRequest HTTP请求
     * @return 更新后的用户信息
     */
    @PutMapping("/update")
    public Response<SysUser> updateUser(@Valid @RequestBody UpdateUserRequest request, 
                                        HttpServletRequest httpRequest) {
        Long userId = UserContext.getUserId(httpRequest);
        SysUser user = sysUserService.updateUser(userId, request);
        return Response.success(user);
    }

    /**
     * 修改密码
     *
     * @param request 修改密码请求信息
     * {
     *     "oldPassword": "旧密码",
     *     "newPassword": "新密码"
     * }
     * @param httpRequest HTTP请求
     * @return 操作结果
     */
    @PutMapping("/change-password")
    public Response<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                          HttpServletRequest httpRequest) {
        Long userId = UserContext.getUserId(httpRequest);
        sysUserService.changePassword(userId, request);
        return Response.success();
    }
}

