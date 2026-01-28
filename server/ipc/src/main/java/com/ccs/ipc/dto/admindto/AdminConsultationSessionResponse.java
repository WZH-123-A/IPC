package com.ccs.ipc.dto.admindto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员端问诊会话响应DTO
 *
 * @author WZH
 * @since 2026-01-28
 */
@Data
public class AdminConsultationSessionResponse {
    private Long id;
    private String sessionNo;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private Byte sessionType; // 1-AI问诊 2-医生问诊
    private String title;
    private Byte status; // 0-进行中 1-已结束 2-已取消
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

