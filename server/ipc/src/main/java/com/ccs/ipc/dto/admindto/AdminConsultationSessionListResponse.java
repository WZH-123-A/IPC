package com.ccs.ipc.dto.admindto;

import lombok.Data;

import java.util.List;

/**
 * 管理员端问诊会话列表响应DTO
 *
 * @author WZH
 * @since 2026-01-28
 */
@Data
public class AdminConsultationSessionListResponse {
    private List<AdminConsultationSessionResponse> records;
    private Long total;
    private Long current;
    private Long size;
}

