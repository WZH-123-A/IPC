package com.ccs.ipc.common.enums.log;

import lombok.Getter;

/**
 * 权限管理模块（SysPermissionController）操作模块
 */
@Getter
public enum PermissionModule implements IOperationModule {
    PERMISSION("权限管理");

    /** 供 @Log 注解使用的编译期常量 */
    public static final class C {
        public static final String PERMISSION = "权限管理";
    }

    private final String value;

    PermissionModule(String value) {
        this.value = value;
    }
}
