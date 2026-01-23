package com.ccs.ipc.controller;

import com.ccs.ipc.common.annotation.Log;
import com.ccs.ipc.common.annotation.RequirePermission;
import com.ccs.ipc.common.enums.OperationModule;
import com.ccs.ipc.common.enums.OperationType;
import com.ccs.ipc.common.response.Response;
import com.ccs.ipc.dto.OperationLogListRequest;
import com.ccs.ipc.dto.OperationLogListResponse;
import com.ccs.ipc.dto.OperationLogResponse;
import com.ccs.ipc.service.ISysOperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 操作日志管理控制器
 *
 * @author WZH
 * @since 2026-01-19
 */
@RestController
@RequestMapping("/api/operation-log")
public class OperationLogController {

    @Autowired
    private ISysOperationLogService sysOperationLogService;

    /**
     * 分页查询操作日志列表
     */
    @GetMapping("/list")
    @RequirePermission("api:operation-log:list")
    public Response<OperationLogListResponse> getOperationLogList(OperationLogListRequest request) {
        OperationLogListResponse response = sysOperationLogService.getOperationLogList(request);
        return Response.success(response);
    }

    /**
     * 根据ID获取操作日志详情
     */
    @GetMapping("/{id}")
    @RequirePermission("api:operation-log:detail")
    public Response<OperationLogResponse> getOperationLogById(@PathVariable Long id) {
        OperationLogResponse response = sysOperationLogService.getOperationLogById(id);
        if (response == null) {
            return Response.fail("操作日志不存在");
        }
        return Response.success(response);
    }

    /**
     * 删除操作日志
     */
    @DeleteMapping("/{id}")
    @RequirePermission("api:operation-log:delete")
    @Log(operationType = OperationType.DELETE, operationModule = OperationModule.LOG, operationDesc = "删除操作日志")
    public Response<Void> deleteOperationLog(@PathVariable Long id) {
        sysOperationLogService.removeById(id);
        return Response.success();
    }

    /**
     * 批量删除操作日志
     */
    @DeleteMapping("/batch")
    @RequirePermission("api:operation-log:delete")
    @Log(operationType = OperationType.DELETE, operationModule = OperationModule.LOG, operationDesc = "批量删除操作日志")
    public Response<Void> batchDeleteOperationLog(@RequestBody java.util.List<Long> ids) {
        sysOperationLogService.removeByIds(ids);
        return Response.success();
    }
}

