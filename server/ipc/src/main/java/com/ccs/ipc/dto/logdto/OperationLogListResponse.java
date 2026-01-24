package com.ccs.ipc.dto.logdto;

import lombok.Data;

import java.util.List;

/**
 * 操作日志列表响应DTO
 *
 * @author WZH
 * @since 2026-01-23
 */
@Data
public class OperationLogListResponse {

    /**
     * 数据列表
     */
    private List<OperationLogResponse> records;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Long current;

    /**
     * 每页大小
     */
    private Long size;
}

