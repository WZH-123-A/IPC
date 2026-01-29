package com.ccs.ipc.dto.patientdto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 问诊评价响应（患者端）
 *
 * @author WZH
 * @since 2026-01-29
 */
@Data
public class EvaluationResponse {

    private Long id;
    private Long sessionId;
    private Byte rating;
    private String comment;
    private LocalDateTime createTime;
}
