package com.ccs.ipc.dto.admindto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 管理员端问诊评价新增请求
 */
@Data
public class AdminConsultationEvaluationCreateRequest {
    @NotNull(message = "会话ID不能为空")
    private Long sessionId;
    @NotNull(message = "患者ID不能为空")
    private Long patientId;
    private Long doctorId;
    @NotNull(message = "评分不能为空")
    @Min(1)
    @Max(5)
    private Byte rating;
    private String comment;
}
