package com.ccs.ipc.common.enums.log;

import lombok.Getter;

/**
 * 认证模块（AuthController）操作方法
 */
@Getter
public enum AuthOperation implements IOperationType {
    LOGIN("登录"),
    GET_PERMISSION_TREE("获取当前用户权限树"),
    GET_MENUS("获取当前用户菜单权限树"),
    GET_BUTTONS("获取当前用户按钮权限列表");

    /** 供 @Log 注解使用的编译期常量（注解值须为常量表达式） */
    public static final class C {
        public static final String LOGIN = "登录";
        public static final String GET_PERMISSION_TREE = "获取当前用户权限树";
        public static final String GET_MENUS = "获取当前用户菜单权限树";
        public static final String GET_BUTTONS = "获取当前用户按钮权限列表";
    }

    private final String value;

    AuthOperation(String value) {
        this.value = value;
    }
}
