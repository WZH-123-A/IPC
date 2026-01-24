package com.ccs.ipc.dto.userdto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 重置密码请求DTO
 *
 * @author WZH
 * @since 2026-01-19
 */
@Data
public class ResetPasswordRequest {
    @NotBlank(message = "新密码不能为空")
    private String password;
}

