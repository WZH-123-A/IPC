package com.ccs.ipc.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysUserResponse {
    private Long id;
    private String username;
    private String password;
    private String phone;
    private String email;
    private String realName;
    private Byte gender;
    private String avatar;
    private Byte status;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Byte isDeleted;
}
