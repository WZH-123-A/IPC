package com.ccs.ipc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 疾病类型表
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("disease_type")
public class DiseaseType implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 疾病ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 疾病编码
     */
    private String diseaseCode;

    /**
     * 疾病名称
     */
    private String diseaseName;

    /**
     * 疾病分类（如：湿疹、皮炎、真菌感染等）
     */
    private String diseaseCategory;

    /**
     * 疾病描述
     */
    private String description;

    /**
     * 常见症状
     */
    private String symptoms;

    /**
     * 治疗方法
     */
    private String treatment;

    /**
     * 预防措施
     */
    private String prevention;

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
