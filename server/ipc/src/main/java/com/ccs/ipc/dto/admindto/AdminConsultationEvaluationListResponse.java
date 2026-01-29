package com.ccs.ipc.dto.admindto;

import lombok.Data;

import java.util.List;

/**
 * 管理员端问诊评价列表响应
 */
@Data
public class AdminConsultationEvaluationListResponse {
    private List<AdminConsultationEvaluationResponse> records;
    private Long total;
    private Long current;
    private Long size;
}
