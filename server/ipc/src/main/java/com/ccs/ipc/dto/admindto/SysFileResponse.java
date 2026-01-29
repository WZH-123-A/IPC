package com.ccs.ipc.dto.admindto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件详情响应 DTO
 *
 * @author WZH
 * @since 2026-01-29
 */
@Data
public class SysFileResponse {

    private Long id;
    private String fileName;
    private String filePath;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private String mimeType;
    private Long uploadUserId;
    private String businessType;
    private Long businessId;
    private LocalDateTime createTime;
}
