package com.ccs.ipc.dto.doctordto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 问诊列表查询请求DTO
 *
 * @author WZH
 * @since 2026-01-27
 */
@Data
public class ConsultationListRequest {
    /**
     * 当前页码
     */
    private Integer current = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;

    /**
     * 患者姓名（模糊查询）
     */
    private String patientName;

    /**
     * 问诊标题（模糊查询）
     */
    private String title;

    /**
     * 状态：0-进行中 1-已结束 2-已取消
     */
    private Byte status;

    /**
     * 会话类型：1-AI问诊 2-医生问诊
     */
    private Byte sessionType;

    /**
     * 开始时间（查询范围起始）
     */
    private LocalDateTime startTime;

    /**
     * 结束时间（查询范围结束）
     */
    private LocalDateTime endTime;
}
