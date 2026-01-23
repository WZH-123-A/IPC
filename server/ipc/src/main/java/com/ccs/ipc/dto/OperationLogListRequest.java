package com.ccs.ipc.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 操作日志查询请求DTO
 *
 * @author WZH
 * @since 2026-01-23
 */
@Data
public class OperationLogListRequest {

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
     * 操作类型（精确查询）
     */
    private String operationType;

    /**
     * 操作模块（精确查询）
     */
    private String operationModule;

    /**
     * 状态（精确查询）：0-失败 1-成功
     */
    private Byte status;

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

