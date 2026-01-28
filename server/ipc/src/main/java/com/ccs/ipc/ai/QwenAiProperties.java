package com.ccs.ipc.ai;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 千问（DashScope / Qwen）配置（OpenAI 兼容接口）。
 *
 * 注意：请勿把 API Key 写死在代码或前端；推荐通过环境变量注入。
 */
@Data
@Component
@ConfigurationProperties(prefix = "ipc.ai.qwen")
public class QwenAiProperties {

    /**
     * API Key（建议通过环境变量 QWEN_API_KEY 注入）
     */
    private String apiKey;

    /**
     * OpenAI 兼容模式 Chat Completions 地址
     */
    private String baseUrl = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";

    /**
     * 模型名称（例如：qwen-plus / qwen-max / qwen-turbo 等）
     */
    private String model = "qwen-plus";

    /**
     * HTTP 超时（毫秒）
     */
    private Integer timeoutMs = 30000;

    /**
     * 构造上下文时最多取多少条历史消息
     */
    private Integer maxHistoryMessages = 20;

    /**
     * 系统提示词（可按项目需要调整）
     */
    private String systemPrompt =
            "你是一名严谨的医疗健康问诊助手。你需要：\n" +
            "1) 先用简短问题补充关键信息（年龄/性别/部位/持续时间/诱因/伴随症状/既往史/用药/过敏史等）。\n" +
            "2) 给出可能性分析与分诊建议（自我处理/门诊/急诊），并提示不替代医生面诊。\n" +
            "3) 不要编造检查结果；不确定时明确说明。\n" +
            "4) 默认用中文回答，表达清晰、条理化。";
}

