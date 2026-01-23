package com.ccs.ipc.dto;

import lombok.Data;

import java.util.List;

/**
 * 更新角色请求DTO
 *
 * @author WZH
 * @since 2026-01-19
 */
@Data
public class UpdateRoleRequest {
    private String roleCode;
    private String roleName;
    private String roleDesc;
    private List<Long> permissionIds;
}

