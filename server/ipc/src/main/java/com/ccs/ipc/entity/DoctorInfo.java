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
 * 医生扩展信息表
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("doctor_info")
public class DoctorInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联sys_user.id
     */
    private Long userId;

    /**
     * 所属医院
     */
    private String hospital;

    /**
     * 科室（默认皮肤科）
     */
    private String department;

    /**
     * 职称（主任医师/副主任医师等）
     */
    private String title;

    /**
     * 擅长领域
     */
    private String specialty;

    /**
     * 医师资格证编号
     */
    private String licenseNo;

    /**
     * 从业年限
     */
    private Integer workYears;

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

    /**
     * 是否删除：0-否 1-是
     */
    private Byte isDeleted;
}
