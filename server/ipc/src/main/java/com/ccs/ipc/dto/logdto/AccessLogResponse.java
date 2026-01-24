package com.ccs.ipc.dto.logdto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 访问日志响应DTO
 *
 * @author WZH
 * @since 2026-01-23
 */
@Data
public class AccessLogResponse {

    /**
     * 日志ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 请求方法（GET/POST等）
     */
    private String requestMethod;

    /**
     * 请求URL
     */
    private String requestUrl;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * HTTP响应状态码
     */
    private Integer responseStatus;

    /**
     * 执行时间（毫秒）
     */
    private Long executionTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

