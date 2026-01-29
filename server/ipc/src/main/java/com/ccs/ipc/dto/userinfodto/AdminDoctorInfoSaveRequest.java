package com.ccs.ipc.dto.userinfodto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 管理员-医生信息新增/修改请求（userId 必填；新增时该用户须已具医生角色且尚未有医生信息）
 *
 * @author WZH
 * @since 2026-01-29
 */
@Data
public class AdminDoctorInfoSaveRequest {

    /** 关联用户ID（必填） */
    @NotNull(message = "请选择用户")
    private Long userId;

    private String hospital;
    private String department;
    private String title;
    private String specialty;
    private String licenseNo;
    private Integer workYears;
}
