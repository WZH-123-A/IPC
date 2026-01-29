package com.ccs.ipc.common.enums.log;

import lombok.Getter;

/**
 * 管理员首页模块（AdminHomeController）操作模块
 */
@Getter
public enum AdminHomeModule implements IOperationModule {
    ADMIN_HOME("管理员首页");

    /** 供 @Log 注解使用的编译期常量 */
    public static final class C {
        public static final String ADMIN_HOME = "管理员首页";
    }

    private final String value;

    AdminHomeModule(String value) {
        this.value = value;
    }
}
