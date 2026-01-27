package com.ccs.ipc.dto.patientdto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendMessageRequest {
    @NotNull(message = "会话ID不能为空")
    private Long sessionId;
    
    @NotNull(message = "消息类型不能为空")
    private Byte messageType; // 1-文本 2-图片 3-语音 4-视频
    
    @NotNull(message = "消息内容不能为空")
    private String content;
}

