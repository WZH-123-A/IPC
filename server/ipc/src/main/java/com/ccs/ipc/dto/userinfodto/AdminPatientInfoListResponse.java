package com.ccs.ipc.dto.userinfodto;

import lombok.Data;

import java.util.List;

/**
 * 管理员-患者信息列表响应
 *
 * @author WZH
 * @since 2026-01-29
 */
@Data
public class AdminPatientInfoListResponse {

    private List<AdminPatientInfoItem> records;
    private Long total;
    private Long current;
    private Long size;
}
