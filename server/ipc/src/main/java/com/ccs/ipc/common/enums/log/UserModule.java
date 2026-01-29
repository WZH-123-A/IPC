package com.ccs.ipc.common.enums.log;

import lombok.Getter;

/**
 * 用户管理模块（SysUserController）操作模块
 */
@Getter
public enum UserModule implements IOperationModule {
    USER("用户管理");

    /** 供 @Log 注解使用的编译期常量 */
    public static final class C {
        public static final String USER = "用户管理";
    }

    private final String value;

    UserModule(String value) {
        this.value = value;
    }
}
