package com.ccs.ipc.dto;

import lombok.Data;

import java.util.List;

/**
 * 权限树节点DTO
 *
 * @author WZH
 * @since 2026-01-19
 */
@Data
public class PermissionTreeNode {
    private Long id;
    private String permissionCode;
    private String permissionName;
    private Byte permissionType;
    private Long parentId;
    private Integer sort;
    private List<PermissionTreeNode> children;
}

