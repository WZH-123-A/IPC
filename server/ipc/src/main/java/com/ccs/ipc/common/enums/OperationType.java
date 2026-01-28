package com.ccs.ipc.common.enums;

import lombok.Getter;

/**
 * 操作类型枚举
 *
 * @author WZH
 * @since 2026-01-19
 */
@Getter
public enum OperationType {

    /**
     * 登录
     */
    LOGIN("登录"),

    /**
     * 登出
     */
    LOGOUT("登出"),

    /**
     * 查询
     */
    QUERY("查询"),

    /**
     * 新增
     */
    ADD("新增"),

    /**
     * 修改
     */
    UPDATE("修改"),

    /**
     * 修改密码
     * /
     */
    UPDATE_PASSWORD("修改密码"),

    /**
     * 删除
     */
    DELETE("删除"),

    /**
     * 导出
     */
    EXPORT("导出"),

    /**
     * 导入
     */
    IMPORT("导入"),

    /**
     * 授权
     */
    AUTHORIZE("授权"),

    /**
     * 发送消息
     */
    SEND("发送消息"),

    /**
     * 结束会诊
     */
    END_CONSULTATION("结束会诊"),

    /**
     * 其他
     */
    OTHER("其他");

    private final String value;

    OperationType(String value) {
        this.value = value;
    }
}

