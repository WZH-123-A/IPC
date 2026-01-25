package com.ccs.ipc.dto.roledto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysRoleResponse {
    private Long id;

    private String roleCode;

    private String roleName;

    private String roleDesc;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Byte isDeleted;
}
