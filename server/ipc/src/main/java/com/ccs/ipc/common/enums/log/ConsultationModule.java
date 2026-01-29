package com.ccs.ipc.common.enums.log;

import lombok.Getter;

/**
 * 问诊管理模块（ConsultationController）操作模块
 */
@Getter
public enum ConsultationModule implements IOperationModule {
    CONSULTATION("问诊管理");

    /** 供 @Log 注解使用的编译期常量 */
    public static final class C {
        public static final String CONSULTATION = "问诊管理";
    }

    private final String value;

    ConsultationModule(String value) {
        this.value = value;
    }
}
