package com.ccs.ipc.dto.patientdto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 患者提交问诊评价请求
 *
 * @author WZH
 * @since 2026-01-29
 */
@Data
public class SubmitEvaluationRequest {

    /**
     * 评分 1-5 星
     */
    @NotNull(message = "请选择评分")
    @Min(value = 1, message = "评分范围为1-5星")
    @Max(value = 5, message = "评分范围为1-5星")
    private Byte rating;

    /**
     * 评价内容（选填）
     */
    private String comment;
}
