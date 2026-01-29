package com.ccs.ipc.common.enums.log;

import lombok.Getter;

/**
 * 权限管理模块（SysPermissionController）操作方法
 */
@Getter
public enum PermissionOperation implements IOperationType {
    QUERY_TREE("获取所有权限（树形结构）"),
    QUERY_LIST("获取所有权限（列表）"),
    QUERY_DETAIL("根据ID获取权限详情"),
    ADD("新增权限"),
    UPDATE("更新权限"),
    DELETE("删除权限");

    /** 供 @Log 注解使用的编译期常量 */
    public static final class C {
        public static final String QUERY_TREE = "获取所有权限（树形结构）";
        public static final String QUERY_LIST = "获取所有权限（列表）";
        public static final String QUERY_DETAIL = "根据ID获取权限详情";
        public static final String ADD = "新增权限";
        public static final String UPDATE = "更新权限";
        public static final String DELETE = "删除权限";
    }

    private final String value;

    PermissionOperation(String value) {
        this.value = value;
    }
}
