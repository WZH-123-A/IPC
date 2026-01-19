package com.ccs.ipc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 在线问诊会话表
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
@Getter
@Setter
@ToString
@TableName("consultation_session")
public class ConsultationSession implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会话ID
     */
    @TableId(value = "id", type = IdType.AUTO)
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
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除：0-否 1-是
     */
    private Byte isDeleted;
}
