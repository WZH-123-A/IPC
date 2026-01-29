package com.ccs.ipc.common.enums.log;

import lombok.Getter;

/**
 * 角色管理模块（SysRoleController）操作模块
 */
@Getter
public enum RoleModule implements IOperationModule {
    ROLE("角色管理");

    /** 供 @Log 注解使用的编译期常量 */
    public static final class C {
        public static final String ROLE = "角色管理";
    }

    private final String value;

    RoleModule(String value) {
        this.value = value;
    }
}
