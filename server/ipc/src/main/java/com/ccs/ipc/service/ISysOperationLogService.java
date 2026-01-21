package com.ccs.ipc.service;

import com.ccs.ipc.entity.SysOperationLog;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 系统操作日志表 服务类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
public interface ISysOperationLogService extends IService<SysOperationLog> {

    /**
     * 异步保存操作日志
     *
     * @param operationLog 操作日志
     */
    void saveAsync(SysOperationLog operationLog);
}
