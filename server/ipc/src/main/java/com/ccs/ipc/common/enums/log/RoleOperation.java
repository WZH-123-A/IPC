package com.ccs.ipc.common.enums.log;

import lombok.Getter;

/**
 * 角色管理模块（SysRoleController）操作方法
 */
@Getter
public enum RoleOperation implements IOperationType {
    QUERY_LIST("分页查询角色列表"),
    QUERY_ALL("获取所有角色（不分页）"),
    QUERY_DETAIL("根据ID获取角色详情"),
    ADD("新增角色"),
    UPDATE("更新角色"),
    DELETE("删除角色"),
    QUERY_PERMISSION_IDS("获取角色的权限ID列表");

    /** 供 @Log 注解使用的编译期常量 */
    public static final class C {
        public static final String QUERY_LIST = "分页查询角色列表";
        public static final String QUERY_ALL = "获取所有角色（不分页）";
        public static final String QUERY_DETAIL = "根据ID获取角色详情";
        public static final String ADD = "新增角色";
        public static final String UPDATE = "更新角色";
        public static final String DELETE = "删除角色";
        public static final String QUERY_PERMISSION_IDS = "获取角色的权限ID列表";
    }

    private final String value;

    RoleOperation(String value) {
        this.value = value;
    }
}
