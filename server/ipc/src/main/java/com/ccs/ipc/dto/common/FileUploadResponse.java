package com.ccs.ipc.dto.common;

import lombok.Data;

/**
 * 文件上传响应
 */
@Data
public class FileUploadResponse {
    private Long fileId;
    private String fileName;
    private String fileUrl;
    private String mimeType;
    private Long fileSize;
    private String fileType;
}

