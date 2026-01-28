package com.ccs.ipc.common.enums;

import lombok.Getter;

/**
 * 操作模块枚举
 *
 * @author WZH
 * @since 2026-01-19
 */
@Getter
public enum OperationModule {

    /**
     * 认证管理
     */
    AUTH("认证管理"),

    /**
     * 用户管理
     */
    USER("用户管理"),

    /**
     * 角色管理
     */
    ROLE("角色管理"),

    /**
     * 权限管理
     */
    PERMISSION("权限管理"),

    /**
     * 知识库管理
     */
    KNOWLEDGE("知识库管理"),

    /**
     * 问诊管理
     */
    CONSULTATION("问诊管理"),

    /**
     * 诊断管理
     */
    DIAGNOSIS("诊断管理"),

    /**
     * 文件管理
     */
    FILE("文件管理"),

    /**
     * 日志管理
     */
    LOG("日志管理"),

    /**
     * 系统配置
     */
    SYSTEM("系统配置"),

    /**
     * 问诊
     */
    CONSULTATION_SESSION("问诊会话"),

    /**
     * 其他
     */
    OTHER("其他");

    private final String value;

    OperationModule(String value) {
        this.value = value;
    }
}

