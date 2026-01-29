package com.ccs.ipc.common.enums.log;

import lombok.Getter;

/**
 * 文件管理模块（FileController）操作模块
 */
@Getter
public enum FileModule implements IOperationModule {
    FILE("文件管理");

    /** 供 @Log 注解使用的编译期常量 */
    public static final class C {
        public static final String FILE = "文件管理";
    }

    private final String value;

    FileModule(String value) {
        this.value = value;
    }
}
