package com.ccs.ipc.dto.permissiondto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysPermissionResponse {
    private Long id;
    private String permissionCode;
    private String permissionName;
    private Byte permissionType;
    private Long parentId;
    private Integer sort;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Byte isDeleted;
}
