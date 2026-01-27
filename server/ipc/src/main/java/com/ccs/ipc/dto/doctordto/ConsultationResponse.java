package com.ccs.ipc.dto.doctordto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 问诊信息响应DTO
 *
 * @author WZH
 * @since 2026-01-27
 */
@Data
public class ConsultationResponse {
    /**
     * 会话ID
     */
    private Long id;

    /**
     * 会话编号
     */
    private String sessionNo;

    /**
     * 患者ID
     */
    private Long patientId;

    /**
     * 患者姓名
     */
    private String patientName;

    /**
     * 患者头像
     */
    private String patientAvatar;

    /**
     * 医生ID（NULL表示AI问诊）
     */
    private Long doctorId;

    /**
     * 会话类型：1-AI问诊 2-医生问诊
     */
    private Byte sessionType;

    /**
     * 问诊标题/主诉
     */
    private String title;

    /**
     * 状态：0-进行中 1-已结束 2-已取消
     */
    private Byte status;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 未读消息数
     */
    private Integer unreadCount;
}
