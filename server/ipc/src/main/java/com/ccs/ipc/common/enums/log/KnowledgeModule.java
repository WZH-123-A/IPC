package com.ccs.ipc.common.enums.log;

import lombok.Getter;

/**
 * 知识库管理模块（KnowledgeController）操作模块
 */
@Getter
public enum KnowledgeModule implements IOperationModule {
    KNOWLEDGE("知识库管理"),
    FILE("文件管理");

    /** 供 @Log 注解使用的编译期常量 */
    public static final class C {
        public static final String KNOWLEDGE = "知识库管理";
        public static final String FILE = "文件管理";
    }

    private final String value;

    KnowledgeModule(String value) {
        this.value = value;
    }
}
