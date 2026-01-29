package com.ccs.ipc.common.enums.log;

import lombok.Getter;

/**
 * 认证模块（AuthController）操作模块
 */
@Getter
public enum AuthModule implements IOperationModule {
    AUTH("认证管理");

    /** 供 @Log 注解使用的编译期常量 */
    public static final class C {
        public static final String AUTH = "认证管理";
    }

    private final String value;

    AuthModule(String value) {
        this.value = value;
    }
}
