package com.ccs.ipc.dto.userinfodto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 管理员-患者信息新增/修改请求（userId 必填；新增时该用户须已具患者角色且尚未有患者信息）
 *
 * @author WZH
 * @since 2026-01-29
 */
@Data
public class AdminPatientInfoSaveRequest {

    /** 关联用户ID（必填） */
    @NotNull(message = "请选择用户")
    private Long userId;

    private Integer age;
    private String address;
    private String medicalHistory;
    private String allergyHistory;
}
