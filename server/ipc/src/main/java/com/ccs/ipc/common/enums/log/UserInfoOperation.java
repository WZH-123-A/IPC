package com.ccs.ipc.common.enums.log;

import lombok.Getter;

/**
 * 用户信息管理模块（UserInfoController）操作方法
 *
 * @author WZH
 * @since 2026-01-29
 */
@Getter
public enum UserInfoOperation implements IOperationType {
    DOCTOR_LIST("查询医生信息列表"),
    DOCTOR_USER_OPTIONS("查询医生用户选项"),
    DOCTOR_CREATE("新增医生信息"),
    DOCTOR_UPDATE("更新医生信息"),
    DOCTOR_DELETE("删除医生信息"),
    PATIENT_LIST("查询患者信息列表"),
    PATIENT_USER_OPTIONS("查询患者用户选项"),
    PATIENT_CREATE("新增患者信息"),
    PATIENT_UPDATE("更新患者信息"),
    PATIENT_DELETE("删除患者信息");

    /** 供 @Log 注解使用的编译期常量 */
    public static final class C {
        public static final String DOCTOR_LIST = "查询医生信息列表";
        public static final String DOCTOR_USER_OPTIONS = "查询医生用户选项";
        public static final String DOCTOR_CREATE = "新增医生信息";
        public static final String DOCTOR_UPDATE = "更新医生信息";
        public static final String DOCTOR_DELETE = "删除医生信息";
        public static final String PATIENT_LIST = "查询患者信息列表";
        public static final String PATIENT_USER_OPTIONS = "查询患者用户选项";
        public static final String PATIENT_CREATE = "新增患者信息";
        public static final String PATIENT_UPDATE = "更新患者信息";
        public static final String PATIENT_DELETE = "删除患者信息";
    }

    private final String value;

    UserInfoOperation(String value) {
        this.value = value;
    }
}
