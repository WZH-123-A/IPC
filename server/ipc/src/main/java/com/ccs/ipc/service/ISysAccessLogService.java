package com.ccs.ipc.service;

import com.ccs.ipc.dto.AccessLogListRequest;
import com.ccs.ipc.dto.AccessLogListResponse;
import com.ccs.ipc.dto.AccessLogResponse;
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

    /**
     * 分页查询访问日志列表
     *
     * @param request 查询请求
     * @return 分页响应
     */
    AccessLogListResponse getAccessLogList(AccessLogListRequest request);

    /**
     * 根据ID获取访问日志详情
     *
     * @param id 日志ID
     * @return 访问日志响应
     */
    AccessLogResponse getAccessLogById(Long id);
}

