package com.ccs.ipc.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 创建角色请求DTO
 *
 * @author WZH
 * @since 2026-01-19
 */
@Data
public class CreateRoleRequest {
    @NotBlank(message = "角色编码不能为空")
    private String roleCode;

    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    private String roleDesc;

    private List<Long> permissionIds;
}

