package com.ccs.ipc.dto.userinfodto;

import lombok.Data;

import java.util.List;

/**
 * 管理员-医生信息列表响应
 *
 * @author WZH
 * @since 2026-01-29
 */
@Data
public class AdminDoctorInfoListResponse {

    private List<AdminDoctorInfoItem> records;
    private Long total;
    private Long current;
    private Long size;
}
