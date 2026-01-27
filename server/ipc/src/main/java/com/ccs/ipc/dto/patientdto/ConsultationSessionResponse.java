package com.ccs.ipc.dto.patientdto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ConsultationSessionResponse {
    private Long id;
    private String sessionNo;
    private Long patientId;
    private Long doctorId;
    private Byte sessionType; // 1-AI问诊 2-医生问诊
    private String title;
    private Byte status; // 0-进行中 1-已结束 2-已取消
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

