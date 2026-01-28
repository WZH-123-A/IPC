package com.ccs.ipc.ai;

import com.ccs.ipc.ai.QwenChatClient.QwenMessage;
import com.ccs.ipc.entity.ConsultationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * AI问诊业务：把历史消息拼接为 Qwen Chat 消息并生成回复。
 */
@Service
@RequiredArgsConstructor
public class QwenAiService {

    private final QwenAiProperties properties;
    private final QwenChatClient chatClient;

    public String generateReply(String sessionTitle, List<ConsultationMessage> history) {
        List<QwenMessage> messages = new ArrayList<>();

        String title = StringUtils.hasText(sessionTitle) ? sessionTitle.trim() : "未命名问诊";
        String systemPrompt = (properties.getSystemPrompt() == null ? "" : properties.getSystemPrompt().trim());
        if (StringUtils.hasText(systemPrompt)) {
            messages.add(QwenMessage.of("system", systemPrompt + "\n\n本次问诊主题/主诉：" + title));
        } else {
            messages.add(QwenMessage.of("system", "本次问诊主题/主诉：" + title));
        }

        if (history != null) {
            for (ConsultationMessage m : history) {
                if (m == null) continue;
                String role = (m.getSenderType() != null && m.getSenderType() == 1) ? "user" : "assistant";
                String content = normalizeContent(m);
                if (!StringUtils.hasText(content)) continue;
                messages.add(QwenMessage.of(role, content));
            }
        }

        return chatClient.chat(messages);
    }

    private String normalizeContent(ConsultationMessage m) {
        if (m.getMessageType() == null) return m.getContent();
        // 仅文本直传；多媒体以占位符进入上下文，避免模型误解为URL内容
        return switch (m.getMessageType()) {
            case 1 -> m.getContent();
            case 2 -> "[图片] " + safe(m.getContent());
            case 3 -> "[语音] " + safe(m.getContent());
            case 4 -> "[视频] " + safe(m.getContent());
            default -> safe(m.getContent());
        };
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}

