package com.ccs.ipc.common.enums.log;

import lombok.Getter;

/**
 * 文件管理模块（FileController）操作方法
 */
@Getter
public enum FileOperation implements IOperationType {
    QUERY_LIST("分页查询文件列表"),
    QUERY_DETAIL("根据ID获取文件详情"),
    DELETE("删除文件记录"),
    BATCH_DELETE("批量删除文件记录");

    /** 供 @Log 注解使用的编译期常量 */
    public static final class C {
        public static final String QUERY_LIST = "分页查询文件列表";
        public static final String QUERY_DETAIL = "根据ID获取文件详情";
        public static final String DELETE = "删除文件记录";
        public static final String BATCH_DELETE = "批量删除文件记录";
    }

    private final String value;

    FileOperation(String value) {
        this.value = value;
    }
}
