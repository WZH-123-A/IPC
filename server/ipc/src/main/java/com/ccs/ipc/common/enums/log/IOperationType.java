package com.ccs.ipc.common.enums.log;

/**
 * 操作类型标识接口
 * 每个模块有自己对应的操作方法枚举，实现此接口以便统一写入操作日志
 *
 * @author WZH
 * @since 2026-01-29
 */
public interface IOperationType {

    /**
     * 操作类型名称（写入 operation_type 字段）
     */
    String getValue();
}
