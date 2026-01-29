package com.ccs.ipc.common.enums.log;

/**
 * 操作模块标识接口
 * 每个 Controller 对应自己的模块枚举，实现此接口以便统一写入操作日志
 *
 * @author WZH
 * @since 2026-01-29
 */
public interface IOperationModule {

    /**
     * 模块名称（写入 operation_module 字段）
     */
    String getValue();
}
