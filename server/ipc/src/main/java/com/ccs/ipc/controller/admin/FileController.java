package com.ccs.ipc.controller.admin;

import com.ccs.ipc.common.annotation.Log;
import com.ccs.ipc.common.annotation.RequirePermission;
import com.ccs.ipc.common.enums.log.FileModule;
import com.ccs.ipc.common.enums.log.FileOperation;
import com.ccs.ipc.common.response.Response;
import com.ccs.ipc.dto.admindto.SysFileListRequest;
import com.ccs.ipc.dto.admindto.SysFileListResponse;
import com.ccs.ipc.dto.admindto.SysFileResponse;
import com.ccs.ipc.service.ISysFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员 - 文件管理
 *
 * @author WZH
 * @since 2026-01-29
 */
@RestController
@RequestMapping("/api/admin/file")
public class FileController {

    @Autowired
    private ISysFileService sysFileService;

    /**
     * 分页查询文件列表
     */
    @GetMapping("/list")
    @RequirePermission("admin:api:file:list")
    @Log(operationType = FileOperation.C.QUERY_LIST, operationModule = FileModule.C.FILE, operationDesc = "分页查询文件列表")
    public Response<SysFileListResponse> getFileList(SysFileListRequest request) {
        SysFileListResponse response = sysFileService.getFileList(request);
        return Response.success(response);
    }

    /**
     * 根据 ID 获取文件详情
     */
    @GetMapping("/{id}")
    @RequirePermission("admin:api:file:detail")
    @Log(operationType = FileOperation.C.QUERY_DETAIL, operationModule = FileModule.C.FILE, operationDesc = "根据ID获取文件详情")
    public Response<SysFileResponse> getFileById(@PathVariable Long id) {
        SysFileResponse response = sysFileService.getFileById(id);
        if (response == null) {
            return Response.fail("文件记录不存在");
        }
        return Response.success(response);
    }

    /**
     * 删除文件记录
     */
    @DeleteMapping("/{id}")
    @RequirePermission("admin:api:file:delete")
    @Log(operationType = FileOperation.C.DELETE, operationModule = FileModule.C.FILE, operationDesc = "删除文件记录")
    public Response<Void> deleteFile(@PathVariable Long id) {
        sysFileService.removeById(id);
        return Response.success();
    }

    /**
     * 批量删除文件记录
     */
    @DeleteMapping("/batch")
    @RequirePermission("admin:api:file:delete")
    @Log(operationType = FileOperation.C.BATCH_DELETE, operationModule = FileModule.C.FILE, operationDesc = "批量删除文件记录")
    public Response<Void> batchDeleteFile(@RequestBody List<Long> ids) {
        sysFileService.removeByIds(ids);
        return Response.success();
    }
}
