package com.ccs.ipc.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 问诊消息记录表
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("consultation_message")
public class ConsultationMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 会话ID
     */
    private Long sessionId;

    /**
     * 发送者ID（用户ID或0表示AI）
     */
    private Long senderId;

    /**
     * 发送者类型：1-患者 2-医生 3-AI
     */
    private Byte senderType;

    /**
     * 消息类型：1-文本 2-图片 3-语音 4-视频
     */
    private Byte messageType;

    /**
     * 消息内容（文本或文件URL）
     */
    private String content;

    /**
     * AI模型名称（如：讯飞星火）
     */
    private String aiModel;

    /**
     * 是否已读：0-未读 1-已读
     */
    private Byte isRead;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 是否删除：0-否 1-是
     */
    private Byte isDeleted;
}
