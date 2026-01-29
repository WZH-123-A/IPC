package com.ccs.ipc.dto.admindto;

import lombok.Data;

/**
 * 管理员端问诊会话更新请求
 */
@Data
public class AdminConsultationSessionUpdateRequest {
    private String title;
    private Byte status; // 0-进行中 1-已结束 2-已取消
}
