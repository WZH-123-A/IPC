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
 * 皮肤病识别记录表
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
@Getter
@Setter
@ToString
@TableName("skin_diagnosis_record")
public class SkinDiagnosisRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 识别记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID（患者）
     */
    private Long userId;

    /**
     * 上传的图片URL
     */
    private String imageUrl;

    /**
     * 图片名称
     */
    private String imageName;

    /**
     * 身体部位（如：面部、手臂、腿部等）
     */
    private String bodyPart;

    /**
     * 使用的模型版本
     */
    private String modelVersion;

    /**
     * 识别状态：0-识别中 1-识别成功 2-识别失败
     */
    private Byte status;

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
