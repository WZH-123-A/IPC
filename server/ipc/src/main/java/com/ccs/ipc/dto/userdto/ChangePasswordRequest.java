package com.ccs.ipc.dto.userdto;

import com.ccs.ipc.common.annotation.Sensitive;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 修改密码请求DTO
 *
 * @author WZH
 * @since 2026-01-19
 */
@Data
public class ChangePasswordRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 旧密码
     */
    @NotBlank(message = "旧密码不能为空")
    @Sensitive(type = Sensitive.SensitiveType.PASSWORD)
    private String oldPassword;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    @Sensitive(type = Sensitive.SensitiveType.PASSWORD)
    private String newPassword;
}

