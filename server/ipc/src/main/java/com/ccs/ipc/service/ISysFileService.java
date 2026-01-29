package com.ccs.ipc.service;

import com.ccs.ipc.dto.admindto.SysFileListRequest;
import com.ccs.ipc.dto.admindto.SysFileListResponse;
import com.ccs.ipc.dto.admindto.SysFileResponse;
import com.ccs.ipc.entity.SysFile;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 文件上传记录表 服务类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
public interface ISysFileService extends IService<SysFile> {

    /**
     * 分页查询文件列表（管理员）
     */
    SysFileListResponse getFileList(SysFileListRequest request);

    /**
     * 根据 ID 获取文件详情（管理员）
     */
    SysFileResponse getFileById(Long id);
}
