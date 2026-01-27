package com.ccs.ipc.dto.patientdto;

import lombok.Data;

@Data
public class ConsultationMessageListRequest {
    private Integer current = 1;
    private Integer size = 50;
}

