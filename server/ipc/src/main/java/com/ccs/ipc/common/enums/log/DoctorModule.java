package com.ccs.ipc.common.enums.log;

import lombok.Getter;

/**
 * 医生端模块（DoctorController）操作模块
 */
@Getter
public enum DoctorModule implements IOperationModule {
    DOCTOR("医生端");

    /** 供 @Log 注解使用的编译期常量 */
    public static final class C {
        public static final String DOCTOR = "医生端";
    }

    private final String value;

    DoctorModule(String value) {
        this.value = value;
    }
}
