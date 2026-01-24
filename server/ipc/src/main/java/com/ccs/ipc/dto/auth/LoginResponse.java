package com.ccs.ipc.dto.auth;

import com.ccs.ipc.common.annotation.Sensitive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 登录响应DTO
 *
 * @author WZH
 * @since 2026-01-19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * JWT Token
     */
    private String token;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    @Sensitive(type = Sensitive.SensitiveType.NAME)
    private String realName;

    /**
     * 角色编码列表（如：["patient", "doctor"]）
     */
    private List<String> roles;

    /**
     * 权限编码列表（如：["patient:home", "patient:consultation"]）
     */
    private List<String> permissions;
}

