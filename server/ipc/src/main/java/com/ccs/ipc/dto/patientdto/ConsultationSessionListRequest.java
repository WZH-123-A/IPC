package com.ccs.ipc.dto.patientdto;

import lombok.Data;

@Data
public class ConsultationSessionListRequest {
    private Integer current = 1;
    private Integer size = 10;
    private Byte status; // 0-进行中 1-已结束 2-已取消
}

