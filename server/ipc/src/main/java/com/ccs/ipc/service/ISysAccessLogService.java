package com.ccs.ipc.service;

import com.ccs.ipc.entity.SysAccessLog;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 系统接口访问日志表 服务类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
public interface ISysAccessLogService extends IService<SysAccessLog> {

    /**
     * 异步保存访问日志
     *
     * @param accessLog 访问日志
     */
    void saveAsync(SysAccessLog accessLog);
}

