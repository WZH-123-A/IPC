package com.ccs.ipc.common.enums.log;

import lombok.Getter;

/**
 * 操作日志管理模块（OperationLogController）操作模块
 */
@Getter
public enum OperationLogModule implements IOperationModule {
    OPERATION_LOG("操作日志管理");

    /** 供 @Log 注解使用的编译期常量 */
    public static final class C {
        public static final String OPERATION_LOG = "操作日志管理";
    }

    private final String value;

    OperationLogModule(String value) {
        this.value = value;
    }
}
