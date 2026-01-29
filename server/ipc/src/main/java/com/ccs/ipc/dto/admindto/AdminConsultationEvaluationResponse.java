package com.ccs.ipc.dto.admindto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员端问诊评价单项响应
 */
@Data
public class AdminConsultationEvaluationResponse {
    private Long id;
    private Long sessionId;
    private String sessionNo;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private Byte rating;
    private String comment;
    private LocalDateTime createTime;
}
