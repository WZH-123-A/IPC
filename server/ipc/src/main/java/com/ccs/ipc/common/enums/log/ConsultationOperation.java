package com.ccs.ipc.common.enums.log;

import lombok.Getter;

/**
 * 问诊管理模块（ConsultationController）操作方法
 */
@Getter
public enum ConsultationOperation implements IOperationType {
    QUERY_SESSION_LIST("分页查询问诊会话列表"),
    QUERY_SESSION_DETAIL("根据ID获取问诊会话详情"),
    QUERY_MESSAGES("管理员分页查询问诊消息"),
    QUERY_EVALUATION_LIST("分页查询问诊评价列表"),
    UPDATE_SESSION("管理员更新问诊会话"),
    DELETE_SESSION("管理员删除问诊会话"),
    DELETE_MESSAGE("管理员删除问诊消息"),
    ADD_EVALUATION("管理员新增问诊评价"),
    UPDATE_EVALUATION("管理员更新问诊评价"),
    DELETE_EVALUATION("管理员删除问诊评价");

    /** 供 @Log 注解使用的编译期常量 */
    public static final class C {
        public static final String QUERY_SESSION_LIST = "分页查询问诊会话列表";
        public static final String QUERY_SESSION_DETAIL = "根据ID获取问诊会话详情";
        public static final String QUERY_MESSAGES = "管理员分页查询问诊消息";
        public static final String QUERY_EVALUATION_LIST = "分页查询问诊评价列表";
        public static final String UPDATE_SESSION = "管理员更新问诊会话";
        public static final String DELETE_SESSION = "管理员删除问诊会话";
        public static final String DELETE_MESSAGE = "管理员删除问诊消息";
        public static final String ADD_EVALUATION = "管理员新增问诊评价";
        public static final String UPDATE_EVALUATION = "管理员更新问诊评价";
        public static final String DELETE_EVALUATION = "管理员删除问诊评价";
    }

    private final String value;

    ConsultationOperation(String value) {
        this.value = value;
    }
}
