package com.ccs.ipc.common.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * 每个 Controller 使用自己的模块枚举与操作方法枚举，传 getValue() 得到的字符串
 *
 * @author WZH
 * @since 2026-01-19
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    /**
     * 操作类型（各模块操作枚举的 getValue()）
     */
    String operationType();

    /**
     * 操作模块（各 Controller 模块枚举的 getValue()）
     */
    String operationModule();

    /**
     * 操作描述
     */
    String operationDesc() default "";

    /**
     * 是否保存请求参数
     */
    boolean saveRequestData() default true;

    /**
     * 是否保存响应数据
     */
    boolean saveResponseData() default true;
}

