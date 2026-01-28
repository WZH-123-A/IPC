package com.ccs.ipc.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;

/**
 * 千问 OpenAI 兼容接口客户端：/chat/completions
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QwenChatClient {

    private final QwenAiProperties properties;
    private RestTemplate restTemplate;

    private RestTemplate getRestTemplate() {
        if (restTemplate != null) return restTemplate;
        Integer timeoutMs = properties.getTimeoutMs() != null ? properties.getTimeoutMs() : 30000;
        restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofMillis(timeoutMs))
                .setReadTimeout(Duration.ofMillis(timeoutMs))
                .build();
        return restTemplate;
    }

    public String chat(List<QwenMessage> messages) {
        String apiKey = resolveApiKey();
        if (!StringUtils.hasText(apiKey)) {
            throw new IllegalStateException("AI服务未配置：缺少 ipc.ai.qwen.api-key");
        }
        if (!StringUtils.hasText(properties.getBaseUrl())) {
            throw new IllegalStateException("AI服务未配置：缺少 ipc.ai.qwen.base-url");
        }

        QwenChatCompletionsRequest requestBody = new QwenChatCompletionsRequest();
        requestBody.setModel(properties.getModel());
        requestBody.setMessages(messages);
        requestBody.setTemperature(0.6);
        requestBody.setStream(false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(apiKey.trim());

        HttpEntity<QwenChatCompletionsRequest> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<QwenChatCompletionsResponse> response = getRestTemplate().exchange(
                    properties.getBaseUrl(),
                    HttpMethod.POST,
                    entity,
                    QwenChatCompletionsResponse.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new IllegalStateException("AI请求失败，HTTP状态码：" + response.getStatusCode());
            }

            QwenChatCompletionsResponse body = response.getBody();
            if (body == null || body.getChoices() == null || body.getChoices().isEmpty()) {
                throw new IllegalStateException("AI响应为空或无choices");
            }
            QwenChoice choice = body.getChoices().get(0);
            if (choice == null || choice.getMessage() == null || !StringUtils.hasText(choice.getMessage().getContent())) {
                throw new IllegalStateException("AI响应缺少message.content");
            }
            return choice.getMessage().getContent().trim();
        } catch (RestClientException e) {
            log.warn("AI请求异常：{}", e.getMessage());
            throw new IllegalStateException("AI请求异常：" + e.getMessage(), e);
        }
    }

    /**
     * 兼容：优先使用 Spring 配置 `ipc.ai.qwen.api-key`，为空时回退到环境变量/系统属性。
     * 这样即便占位符没生效（或进程未继承环境变量），也更容易定位问题。
     */
    private String resolveApiKey() {
        String key = properties.getApiKey();
        if (StringUtils.hasText(key)) return key;

        key = System.getenv("QWEN_API_KEY");
        if (StringUtils.hasText(key)) return key;

        key = System.getProperty("QWEN_API_KEY");
        if (StringUtils.hasText(key)) return key;

        return null;
    }

    @Data
    public static class QwenMessage {
        private String role; // system/user/assistant
        private String content;

        public static QwenMessage of(String role, String content) {
            QwenMessage m = new QwenMessage();
            m.setRole(role);
            m.setContent(content);
            return m;
        }
    }

    @Data
    public static class QwenChatCompletionsRequest {
        private String model;
        private List<QwenMessage> messages;
        private Double temperature;
        @JsonProperty("max_tokens")
        private Integer maxTokens;
        private Boolean stream;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class QwenChatCompletionsResponse {
        private List<QwenChoice> choices;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class QwenChoice {
        private QwenAssistantMessage message;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class QwenAssistantMessage {
        private String role;
        private String content;
    }
}

