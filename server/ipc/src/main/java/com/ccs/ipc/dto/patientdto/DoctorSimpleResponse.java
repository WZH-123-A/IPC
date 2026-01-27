package com.ccs.ipc.dto.patientdto;

import lombok.Data;

/**
 * 医生简单信息响应DTO（用于患者选择医生）
 *
 * @author WZH
 * @since 2026-01-27
 */
@Data
public class DoctorSimpleResponse {
    /**
     * 医生用户ID
     */
    private Long id;

    /**
     * 医生姓名
     */
    private String realName;

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
     * 从业年限
     */
    private Integer workYears;
}

