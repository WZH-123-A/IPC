package com.ccs.ipc.dto.admindto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 管理员端问诊评价更新请求
 */
@Data
public class AdminConsultationEvaluationUpdateRequest {
    @Min(1)
    @Max(5)
    private Byte rating;
    private String comment;
}
