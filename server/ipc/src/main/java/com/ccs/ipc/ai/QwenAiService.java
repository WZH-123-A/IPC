package com.ccs.ipc.ai;

import com.ccs.ipc.entity.ConsultationMessage;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * AI 问诊业务：基于 LangChain4J 调用千问模型，将历史消息转为多轮对话并生成回复。
 */
@Slf4j
@Service
public class QwenAiService {

    private final QwenAiProperties properties;
    private final ChatModel chatModel;

    public QwenAiService(QwenAiProperties properties,
                         @Autowired(required = false) ChatModel qwenChatModel) {
        this.properties = properties;
        this.chatModel = qwenChatModel;
    }

    /**
     * 根据会话标题与历史消息生成 AI 回复（LangChain4J 多轮对话）
     */
    public String generateReply(String sessionTitle, List<ConsultationMessage> history) {
        if (chatModel == null) {
            throw new IllegalStateException("AI服务未配置：请配置 ipc.ai.qwen.api-key 或环境变量 QWEN_API_KEY");
        }

        List<ChatMessage> messages = new ArrayList<>();

        String title = StringUtils.hasText(sessionTitle) ? sessionTitle.trim() : "未命名问诊";
        String systemPrompt = properties.getSystemPrompt() == null ? "" : properties.getSystemPrompt().trim();
        if (StringUtils.hasText(systemPrompt)) {
            messages.add(new SystemMessage(systemPrompt + "\n\n本次问诊主题/主诉：" + title));
        } else {
            messages.add(new SystemMessage("本次问诊主题/主诉：" + title));
        }

        if (history != null) {
            for (ConsultationMessage m : history) {
                if (m == null) continue;
                String content = normalizeContent(m);
                if (!StringUtils.hasText(content)) continue;
                boolean fromUser = m.getSenderType() != null && m.getSenderType() == 1;
                if (fromUser) {
                    messages.add(UserMessage.from(content));
                } else {
                    messages.add(AiMessage.from(content));
                }
            }
        }

        ChatResponse response = chatModel.chat(messages);
        AiMessage aiMessage = response.aiMessage();
        if (aiMessage == null || aiMessage.text() == null) {
            throw new IllegalStateException("AI响应为空");
        }
        return aiMessage.text().trim();
    }

    private String normalizeContent(ConsultationMessage m) {
        if (m.getMessageType() == null) return m.getContent();
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
