package com.ccs.ipc.common.enums.log;

import lombok.Getter;

/**
 * 管理员首页模块（AdminHomeController）操作方法
 */
@Getter
public enum AdminHomeOperation implements IOperationType {
    GET_ONLINE_COUNT("获取当前在线人数");

    /** 供 @Log 注解使用的编译期常量 */
    public static final class C {
        public static final String GET_ONLINE_COUNT = "获取当前在线人数";
    }

    private final String value;

    AdminHomeOperation(String value) {
        this.value = value;
    }
}
