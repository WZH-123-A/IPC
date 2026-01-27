package com.ccs.ipc.dto.doctordto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 患者信息响应DTO
 *
 * @author WZH
 * @since 2026-01-27
 */
@Data
public class PatientResponse {
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
     * 年龄
     */
    private Integer age;

    /**
     * 住址
     */
    private String address;

    /**
     * 既往病史
     */
    private String medicalHistory;

    /**
     * 过敏史
     */
    private String allergyHistory;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
