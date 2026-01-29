package com.ccs.ipc.common.enums.log;

import lombok.Getter;

/**
 * 用户信息管理模块（UserInfoController：医生/患者扩展信息）操作模块
 *
 * @author WZH
 * @since 2026-01-29
 */
@Getter
public enum UserInfoModule implements IOperationModule {
    USER_INFO("用户信息管理");

    /** 供 @Log 注解使用的编译期常量 */
    public static final class C {
        public static final String USER_INFO = "用户信息管理";
    }

    private final String value;

    UserInfoModule(String value) {
        this.value = value;
    }
}
