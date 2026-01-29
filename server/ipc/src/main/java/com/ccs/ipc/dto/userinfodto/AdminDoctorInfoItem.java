package com.ccs.ipc.dto.userinfodto;

import lombok.Data;

import java.io.Serializable;

/**
 * 管理员-医生信息列表项
 *
 * @author WZH
 * @since 2026-01-29
 */
@Data
public class AdminDoctorInfoItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private String username;
    private String realName;
    private String phone;
    private String hospital;
    private String department;
    private String title;
    private String specialty;
    private String licenseNo;
    private Integer workYears;
    private String createTime;
    private String updateTime;
}
