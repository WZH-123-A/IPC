package com.ccs.ipc.controller.admin;

import com.ccs.ipc.common.annotation.Log;
import com.ccs.ipc.common.annotation.RequirePermission;
import com.ccs.ipc.common.enums.log.AccessLogModule;
import com.ccs.ipc.common.enums.log.AccessLogOperation;
import com.ccs.ipc.common.response.Response;
import com.ccs.ipc.dto.logdto.AccessLogListRequest;
import com.ccs.ipc.dto.logdto.AccessLogListResponse;
import com.ccs.ipc.dto.logdto.AccessLogResponse;
import com.ccs.ipc.service.ISysAccessLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 访问日志管理控制器
 *
 * @author WZH
 * @since 2026-01-19
 */
@RestController
@RequestMapping("/api/admin/access-log")
public class AccessLogController {

    @Autowired
    private ISysAccessLogService sysAccessLogService;

    /**
     * 分页查询访问日志列表
     */
    @GetMapping("/list")
    @RequirePermission("admin:api:access-log:list")
    public Response<AccessLogListResponse> getAccessLogList(AccessLogListRequest request) {
        AccessLogListResponse response = sysAccessLogService.getAccessLogList(request);
        return Response.success(response);
    }

    /**
     * 根据ID获取访问日志详情
     */
    @GetMapping("/{id}")
    @RequirePermission("admin:api:access-log:detail")
    public Response<AccessLogResponse> getAccessLogById(@PathVariable Long id) {
        AccessLogResponse response = sysAccessLogService.getAccessLogById(id);
        if (response == null) {
            return Response.fail("访问日志不存在");
        }
        return Response.success(response);
    }

    /**
     * 删除访问日志
     */
    @DeleteMapping("/{id}")
    @RequirePermission("admin:api:access-log:delete")
    @Log(operationType = AccessLogOperation.C.DELETE, operationModule = AccessLogModule.C.ACCESS_LOG, operationDesc = "删除访问日志")
    public Response<Void> deleteAccessLog(@PathVariable Long id) {
        sysAccessLogService.removeById(id);
        return Response.success();
    }

    /**
     * 批量删除访问日志
     */
    @DeleteMapping("/batch")
    @RequirePermission("admin:api:access-log:delete")
    @Log(operationType = AccessLogOperation.C.BATCH_DELETE, operationModule = AccessLogModule.C.ACCESS_LOG, operationDesc = "批量删除访问日志")
    public Response<Void> batchDeleteAccessLog(@RequestBody java.util.List<Long> ids) {
        sysAccessLogService.removeByIds(ids);
        return Response.success();
    }
}
