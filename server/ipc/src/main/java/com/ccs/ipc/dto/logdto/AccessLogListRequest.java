package com.ccs.ipc.dto.logdto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 访问日志查询请求DTO
 *
 * @author WZH
 * @since 2026-01-23
 */
@Data
public class AccessLogListRequest {

    /**
     * 当前页码，默认1
     */
    private Integer current = 1;

    /**
     * 每页大小，默认10
     */
    private Integer size = 10;

    /**
     * 用户名（模糊查询）
     */
    private String username;

    /**
     * 请求URL（模糊查询）
     */
    private String requestUrl;

    /**
     * 请求方法（精确查询）
     */
    private String requestMethod;

    /**
     * 响应状态码（精确查询）
     */
    private Integer responseStatus;

    /**
     * 开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}

