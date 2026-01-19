package com.ccs.ipc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 识别结果详情表
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
@Getter
@Setter
@ToString
@TableName("diagnosis_result")
public class DiagnosisResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 结果ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 识别记录ID
     */
    private Long recordId;

    /**
     * 疾病ID
     */
    private Long diseaseId;

    /**
     * 置信度（0-100）
     */
    private BigDecimal confidence;

    /**
     * 排名（1为最可能）
     */
    private Integer rank;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
