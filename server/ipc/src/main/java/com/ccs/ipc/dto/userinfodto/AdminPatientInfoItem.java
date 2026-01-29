package com.ccs.ipc.dto.userinfodto;

import lombok.Data;

import java.io.Serializable;

/**
 * 管理员-患者信息列表项
 *
 * @author WZH
 * @since 2026-01-29
 */
@Data
public class AdminPatientInfoItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private String username;
    private String realName;
    private String phone;
    private Integer age;
    private String address;
    private String medicalHistory;
    private String allergyHistory;
    private String createTime;
    private String updateTime;
}
