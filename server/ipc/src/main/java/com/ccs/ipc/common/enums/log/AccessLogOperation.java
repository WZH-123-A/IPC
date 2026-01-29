package com.ccs.ipc.common.enums.log;

import lombok.Getter;

/**
 * 访问日志管理模块（AccessLogController）操作方法
 */
@Getter
public enum AccessLogOperation implements IOperationType {
    DELETE("删除访问日志"),
    BATCH_DELETE("批量删除访问日志");

    /** 供 @Log 注解使用的编译期常量 */
    public static final class C {
        public static final String DELETE = "删除访问日志";
        public static final String BATCH_DELETE = "批量删除访问日志";
    }

    private final String value;

    AccessLogOperation(String value) {
        this.value = value;
    }
}
