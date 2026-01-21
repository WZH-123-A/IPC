package com.ccs.ipc.dto;

import com.ccs.ipc.common.annotation.Sensitive;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新用户信息请求DTO
 *
 * @author WZH
 * @since 2026-01-19
 */
@Data
public class UpdateUserRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 手机号
     */
    @Sensitive(type = Sensitive.SensitiveType.PHONE)
    private String phone;

    /**
     * 邮箱
     */
    @Sensitive(type = Sensitive.SensitiveType.EMAIL)
    private String email;

    /**
     * 真实姓名
     */
    @Sensitive(type = Sensitive.SensitiveType.NAME)
    private String realName;

    /**
     * 性别：0-未知 1-男 2-女
     */
    private Byte gender;

    /**
     * 头像URL
     */
    private String avatar;
}

