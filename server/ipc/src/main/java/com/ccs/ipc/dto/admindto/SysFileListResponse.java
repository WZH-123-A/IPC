package com.ccs.ipc.dto.admindto;

import lombok.Data;

import java.util.List;

/**
 * 文件列表响应 DTO
 *
 * @author WZH
 * @since 2026-01-29
 */
@Data
public class SysFileListResponse {

    private List<SysFileResponse> records;
    private Long total;
    private Long current;
    private Long size;
}
