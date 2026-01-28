package com.ccs.ipc.dto.admindto;

import lombok.Data;

/**
 * 管理员端问诊会话列表查询请求DTO
 *
 * @author WZH
 * @since 2026-01-28
 */
@Data
public class AdminConsultationSessionListRequest {
    private Integer current = 1;
    private Integer size = 10;
    private String sessionNo;
    private Long patientId;
    private Long doctorId;
    private Byte sessionType; // 1-AI问诊 2-医生问诊
    private Byte status; // 0-进行中 1-已结束 2-已取消
}

