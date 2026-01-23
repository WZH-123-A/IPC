package com.ccs.ipc.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建权限请求DTO
 *
 * @author WZH
 * @since 2026-01-19
 */
@Data
public class CreatePermissionRequest {
    @NotBlank(message = "权限编码不能为空")
    private String permissionCode;

    @NotBlank(message = "权限名称不能为空")
    private String permissionName;

    private Byte permissionType;
    private Long parentId;
    private Integer sort;
}

