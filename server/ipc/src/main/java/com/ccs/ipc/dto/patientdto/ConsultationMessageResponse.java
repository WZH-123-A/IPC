package com.ccs.ipc.dto.patientdto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ConsultationMessageResponse {
    private Long id;
    private Long sessionId;
    private Long senderId;
    private Byte senderType; // 1-患者 2-医生 3-AI
    private Byte messageType; // 1-文本 2-图片 3-语音 4-视频
    private String content;
    private String aiModel;
    private Byte isRead;
    private LocalDateTime createTime;
}

