package com.ccs.ipc.dto.admindto;

import lombok.Data;

/**
 * 管理员端问诊评价列表查询请求
 */
@Data
public class AdminConsultationEvaluationListRequest {
    private Integer current = 1;
    private Integer size = 10;
    private Long sessionId;
    private Long patientId;
    private Long doctorId;
    private Byte rating; // 1-5
}
