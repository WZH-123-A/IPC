package com.ccs.ipc.service;

import com.ccs.ipc.dto.logdto.OperationLogListRequest;
import com.ccs.ipc.dto.logdto.OperationLogListResponse;
import com.ccs.ipc.dto.logdto.OperationLogResponse;
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

    /**
     * 分页查询操作日志列表
     *
     * @param request 查询请求
     * @return 分页响应
     */
    OperationLogListResponse getOperationLogList(OperationLogListRequest request);

    /**
     * 根据ID获取操作日志详情
     *
     * @param id 日志ID
     * @return 操作日志响应
     */
    OperationLogResponse getOperationLogById(Long id);
}
