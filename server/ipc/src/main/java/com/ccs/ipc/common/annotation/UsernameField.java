package com.ccs.ipc.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用户名字段注解
 * 用于标记DTO中表示用户名的字段，AOP切面会自动从该字段提取用户名
 *
 * @author WZH
 * @since 2026-01-19
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UsernameField {
}

