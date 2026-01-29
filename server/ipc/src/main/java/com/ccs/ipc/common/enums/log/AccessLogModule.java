package com.ccs.ipc.common.enums.log;

import lombok.Getter;

/**
 * 访问日志管理模块（AccessLogController）操作模块
 */
@Getter
public enum AccessLogModule implements IOperationModule {
    ACCESS_LOG("访问日志管理");

    /** 供 @Log 注解使用的编译期常量 */
    public static final class C {
        public static final String ACCESS_LOG = "访问日志管理";
    }

    private final String value;

    AccessLogModule(String value) {
        this.value = value;
    }
}
