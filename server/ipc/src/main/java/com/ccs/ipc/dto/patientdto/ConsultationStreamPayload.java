package com.ccs.ipc.dto.patientdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI 流式输出 WebSocket 推送载荷
 * 与 ConsultationMessageResponse 同发往 /topic/consultation/{sessionId}，客户端根据 type 区分处理。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationStreamPayload {

    public static final String TYPE_AI_STREAM_START = "ai_stream_start";
    public static final String TYPE_AI_STREAM_CHUNK = "ai_stream_chunk";
    public static final String TYPE_AI_STREAM_END = "ai_stream_end";

    /**
     * 类型：ai_stream_start / ai_stream_chunk / ai_stream_end
     */
    private String type;
    /**
     * 会话 ID
     */
    private Long sessionId;
    /**
     * 流式片段内容（仅 ai_stream_chunk 时有值）
     */
    private String chunk;
    /**
     * 完整消息（仅 ai_stream_end 时有值）
     */
    private ConsultationMessageResponse message;
}
