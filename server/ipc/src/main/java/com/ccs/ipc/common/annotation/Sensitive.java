package com.ccs.ipc.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 敏感字段脱敏注解
 *
 * @author WZH
 * @since 2026-01-19
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Sensitive {

    /**
     * 脱敏类型
     */
    SensitiveType type() default SensitiveType.CUSTOM;

    /**
     * 自定义脱敏规则：前缀保留长度
     */
    int prefixLen() default 0;

    /**
     * 自定义脱敏规则：后缀保留长度
     */
    int suffixLen() default 0;

    /**
     * 脱敏字符
     */
    String maskChar() default "*";

    /**
     * 脱敏类型枚举
     */
    enum SensitiveType {
        /**
         * 密码：全部脱敏
         */
        PASSWORD,
        /**
         * 手机号：保留前3位和后4位
         */
        PHONE,
        /**
         * 邮箱：保留前3位和@后面的部分
         */
        EMAIL,
        /**
         * 身份证：保留前6位和后4位
         */
        ID_CARD,
        /**
         * 银行卡：保留前4位和后4位
         */
        BANK_CARD,
        /**
         * 姓名：保留姓氏，名字脱敏
         */
        NAME,
        /**
         * 地址：保留省市区，详细地址脱敏
         */
        ADDRESS,
        /**
         * 自定义脱敏规则
         */
        CUSTOM
    }
}

