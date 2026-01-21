package com.ccs.ipc.common.annotation;

import com.ccs.ipc.common.enums.OperationModule;
import com.ccs.ipc.common.enums.OperationType;

import java.lang.annotation.*;

/**
 * 操作日志注解
 *
 * @author WZH
 * @since 2026-01-19
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    /**
     * 操作类型
     */
    OperationType operationType();

    /**
     * 操作模块
     */
    OperationModule operationModule();

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

