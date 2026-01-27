package com.ccs.ipc.dto.doctordto;

import lombok.Data;

import java.util.List;

/**
 * 问诊列表响应DTO
 *
 * @author WZH
 * @since 2026-01-27
 */
@Data
public class ConsultationListResponse {
    /**
     * 数据列表
     */
    private List<ConsultationResponse> records;

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
