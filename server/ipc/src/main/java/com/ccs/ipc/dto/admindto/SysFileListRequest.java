package com.ccs.ipc.dto.admindto;

import lombok.Data;

/**
 * 文件列表查询请求 DTO
 *
 * @author WZH
 * @since 2026-01-29
 */
@Data
public class SysFileListRequest {

    /**
     * 当前页码，默认 1
     */
    private Integer current = 1;

    /**
     * 每页大小，默认 10
     */
    private Integer size = 10;

    /**
     * 文件名称（模糊查询）
     */
    private String fileName;

    /**
     * 文件类型（image/video/document 等，精确查询）
     */
    private String fileType;

    /**
     * 业务类型（diagnosis/consultation/knowledge 等，精确查询）
     */
    private String businessType;
}
