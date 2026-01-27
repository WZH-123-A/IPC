package com.ccs.ipc.dto.doctordto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 医生信息响应DTO
 *
 * @author WZH
 * @since 2026-01-27
 */
@Data
public class DoctorInfoResponse {
    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 性别：0-未知 1-男 2-女
     */
    private Byte gender;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 所属医院
     */
    private String hospital;

    /**
     * 科室
     */
    private String department;

    /**
     * 职称
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
    private LocalDateTime createTime;
}
