package com.ccs.ipc.common.enums.log;

import lombok.Getter;

/**
 * 操作日志管理模块（OperationLogController）操作方法
 */
@Getter
public enum OperationLogOperation implements IOperationType {
    QUERY_LIST("分页查询操作日志列表"),
    QUERY_DETAIL("根据ID获取操作日志详情"),
    DELETE("删除操作日志"),
    BATCH_DELETE("批量删除操作日志");

    /** 供 @Log 注解使用的编译期常量 */
    public static final class C {
        public static final String QUERY_LIST = "分页查询操作日志列表";
        public static final String QUERY_DETAIL = "根据ID获取操作日志详情";
        public static final String DELETE = "删除操作日志";
        public static final String BATCH_DELETE = "批量删除操作日志";
    }

    private final String value;

    OperationLogOperation(String value) {
        this.value = value;
    }
}
