package com.ccs.ipc.common.enums.log;

import lombok.Getter;

/**
 * 患者端模块（PatientController）操作方法
 */
@Getter
public enum PatientOperation implements IOperationType {
    QUERY_SESSIONS("获取问诊会话列表"),
    QUERY_SESSION_DETAIL("获取问诊会话详情"),
    QUERY_MESSAGES("获取问诊消息列表"),
    CREATE_SESSION("创建问诊会话"),
    END_SESSION("结束问诊会话"),
    SEND_MESSAGE("发送问诊消息"),
    MARK_MESSAGE_READ("标记消息为已读"),
    MARK_ALL_READ("批量标记消息为已读"),
    QUERY_EVALUATION("获取会话评价"),
    SUBMIT_EVALUATION("提交问诊评价"),
    UPLOAD_CHAT_FILE("上传问诊聊天文件"),
    UPLOAD_DIAGNOSIS_IMAGE("上传诊断图片"),
    QUERY_DIAGNOSIS_RECORDS("获取诊断记录列表"),
    QUERY_DIAGNOSIS_RECORD_DETAIL("获取诊断记录详情"),
    QUERY_DIAGNOSIS_RESULTS("获取诊断结果列表"),
    QUERY_DISEASE_TYPES("获取疾病类型列表"),
    QUERY_DOCTORS("获取可问诊医生列表"),
    QUERY_KNOWLEDGE_CATEGORIES("获取知识库分类列表"),
    QUERY_KNOWLEDGE_CONTENTS("获取知识库内容列表"),
    QUERY_KNOWLEDGE_DETAIL("获取知识库内容详情");

    /** 供 @Log 注解使用的编译期常量 */
    public static final class C {
        public static final String QUERY_SESSIONS = "获取问诊会话列表";
        public static final String QUERY_SESSION_DETAIL = "获取问诊会话详情";
        public static final String QUERY_MESSAGES = "获取问诊消息列表";
        public static final String CREATE_SESSION = "创建问诊会话";
        public static final String END_SESSION = "结束问诊会话";
        public static final String SEND_MESSAGE = "发送问诊消息";
        public static final String MARK_MESSAGE_READ = "标记消息为已读";
        public static final String MARK_ALL_READ = "批量标记消息为已读";
        public static final String QUERY_EVALUATION = "获取会话评价";
        public static final String SUBMIT_EVALUATION = "提交问诊评价";
        public static final String UPLOAD_CHAT_FILE = "上传问诊聊天文件";
        public static final String UPLOAD_DIAGNOSIS_IMAGE = "上传诊断图片";
        public static final String QUERY_DIAGNOSIS_RECORDS = "获取诊断记录列表";
        public static final String QUERY_DIAGNOSIS_RECORD_DETAIL = "获取诊断记录详情";
        public static final String QUERY_DIAGNOSIS_RESULTS = "获取诊断结果列表";
        public static final String QUERY_DISEASE_TYPES = "获取疾病类型列表";
        public static final String QUERY_DOCTORS = "获取可问诊医生列表";
        public static final String QUERY_KNOWLEDGE_CATEGORIES = "获取知识库分类列表";
        public static final String QUERY_KNOWLEDGE_CONTENTS = "获取知识库内容列表";
        public static final String QUERY_KNOWLEDGE_DETAIL = "获取知识库内容详情";
    }

    private final String value;

    PatientOperation(String value) {
        this.value = value;
    }
}
