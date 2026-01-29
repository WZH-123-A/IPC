package com.ccs.ipc.common.enums.log;

import lombok.Getter;

/**
 * 医生端模块（DoctorController）操作方法
 */
@Getter
public enum DoctorOperation implements IOperationType {
    QUERY_HOME("获取医生首页数据"),
    QUERY_PATIENT_LIST("分页查询患者列表"),
    QUERY_CONSULTATION_LIST("分页查询问诊列表"),
    QUERY_MESSAGES("获取问诊消息列表"),
    SEND_MESSAGE("医生发送消息"),
    UPLOAD_FILE("医生上传问诊聊天文件"),
    END_CONSULTATION("医生结束问诊"),
    MARK_MESSAGES_READ("医生批量标记消息为已读");

    /** 供 @Log 注解使用的编译期常量 */
    public static final class C {
        public static final String QUERY_HOME = "获取医生首页数据";
        public static final String QUERY_PATIENT_LIST = "分页查询患者列表";
        public static final String QUERY_CONSULTATION_LIST = "分页查询问诊列表";
        public static final String QUERY_MESSAGES = "获取问诊消息列表";
        public static final String SEND_MESSAGE = "医生发送消息";
        public static final String UPLOAD_FILE = "医生上传问诊聊天文件";
        public static final String END_CONSULTATION = "医生结束问诊";
        public static final String MARK_MESSAGES_READ = "医生批量标记消息为已读";
    }

    private final String value;

    DoctorOperation(String value) {
        this.value = value;
    }
}
