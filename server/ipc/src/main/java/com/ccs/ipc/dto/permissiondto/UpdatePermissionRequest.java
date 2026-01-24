package com.ccs.ipc.dto.permissiondto;

import lombok.Data;

/**
 * 更新权限请求DTO
 *
 * @author WZH
 * @since 2026-01-19
 */
@Data
public class UpdatePermissionRequest {
    private String permissionName;
    private Byte permissionType;
    private Long parentId;
    private Integer sort;
}

