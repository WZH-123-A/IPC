package com.ccs.ipc.common.enums.log;

import lombok.Getter;

/**
 * 用户管理模块（SysUserController）操作方法
 */
@Getter
public enum UserOperation implements IOperationType {
    QUERY_LIST("分页查询用户列表"),
    QUERY_DETAIL("根据ID获取用户详情"),
    QUERY_ROLES("获取用户的角色ID列表"),
    ADD("新增用户"),
    UPDATE("更新用户"),
    UPDATE_INFO("更新用户信息"),
    DELETE("删除用户"),
    RESET_PASSWORD("重置用户密码"),
    CHANGE_PASSWORD("修改密码");

    /** 供 @Log 注解使用的编译期常量 */
    public static final class C {
        public static final String QUERY_LIST = "分页查询用户列表";
        public static final String QUERY_DETAIL = "根据ID获取用户详情";
        public static final String QUERY_ROLES = "获取用户的角色ID列表";
        public static final String ADD = "新增用户";
        public static final String UPDATE = "更新用户";
        public static final String UPDATE_INFO = "更新用户信息";
        public static final String DELETE = "删除用户";
        public static final String RESET_PASSWORD = "重置用户密码";
        public static final String CHANGE_PASSWORD = "修改密码";
    }

    private final String value;

    UserOperation(String value) {
        this.value = value;
    }
}
