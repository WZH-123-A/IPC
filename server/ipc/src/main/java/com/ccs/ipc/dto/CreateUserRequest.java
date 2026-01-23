package com.ccs.ipc.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 创建用户请求DTO（管理员功能）
 *
 * @author WZH
 * @since 2026-01-19
 */
@Data
public class CreateUserRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    private String phone;
    private String email;
    private String realName;
    private Byte gender;
    private Byte status;
    private List<Long> roleIds;
}

