package com.ccs.ipc.dto.userinfodto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户下拉项（id、用户名、真实姓名）
 *
 * @author WZH
 * @since 2026-01-29
 */
@Data
public class UserOptionItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String realName;
}
