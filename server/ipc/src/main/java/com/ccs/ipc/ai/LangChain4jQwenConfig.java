package com.ccs.ipc.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.time.Duration;

/**
 * LangChain4J 集成千问（Qwen）配置
 * 通过 OpenAI 兼容接口（DashScope compatible-mode）调用千问模型
 *
 * @author WZH
 * @since 2026-01-29
 */
@Configuration
public class LangChain4jQwenConfig {

    /**
     * 当配置了 ipc.ai.qwen.api-key 或环境变量 QWEN_API_KEY 时创建千问 ChatModel
     */
    @Bean
    @ConditionalOnProperty(name = "ipc.ai.qwen.api-key")
    public ChatModel qwenChatModel(QwenAiProperties properties) {
        String apiKey = resolveApiKey(properties);
        if (!StringUtils.hasText(apiKey)) {
            throw new IllegalStateException("AI服务未配置：缺少 ipc.ai.qwen.api-key 或环境变量 QWEN_API_KEY");
        }
        String baseUrl = properties.getBaseUrl();
        if (!StringUtils.hasText(baseUrl)) {
            baseUrl = "https://dashscope.aliyuncs.com/compatible-mode/v1";
        } else if (baseUrl.endsWith("/chat/completions")) {
            baseUrl = baseUrl.replace("/chat/completions", "");
        }
        String modelName = StringUtils.hasText(properties.getModel()) ? properties.getModel() : "qwen-plus";
        int timeoutMs = properties.getTimeoutMs() != null ? properties.getTimeoutMs() : 30000;

        return OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey.trim())
                .modelName(modelName)
                .temperature(0.6)
                .timeout(Duration.ofMillis(timeoutMs))
                .build();
    }

    private static String resolveApiKey(QwenAiProperties properties) {
        if (StringUtils.hasText(properties.getApiKey())) {
            return properties.getApiKey();
        }
        String key = System.getenv("QWEN_API_KEY");
        if (StringUtils.hasText(key)) return key;
        key = System.getProperty("QWEN_API_KEY");
        return key;
    }
}
