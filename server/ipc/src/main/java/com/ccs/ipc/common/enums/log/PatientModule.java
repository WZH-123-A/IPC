package com.ccs.ipc.common.enums.log;

import lombok.Getter;

/**
 * 患者端模块（PatientController）操作模块
 */
@Getter
public enum PatientModule implements IOperationModule {
    PATIENT("患者端");

    /** 供 @Log 注解使用的编译期常量 */
    public static final class C {
        public static final String PATIENT = "患者端";
    }

    private final String value;

    PatientModule(String value) {
        this.value = value;
    }
}
