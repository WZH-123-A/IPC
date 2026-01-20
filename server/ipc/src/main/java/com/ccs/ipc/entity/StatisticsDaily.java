package com.ccs.ipc.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 每日统计数据表
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("statistics_daily")
public class StatisticsDaily implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 统计ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 统计日期
     */
    private LocalDate statDate;

    /**
     * 统计类型（user_register/consultation/diagnosis/knowledge_view等）
     */
    private String statType;

    /**
     * 统计值
     */
    private Long statValue;

    /**
     * 扩展数据（JSON格式）
     */
    private String extraData;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
