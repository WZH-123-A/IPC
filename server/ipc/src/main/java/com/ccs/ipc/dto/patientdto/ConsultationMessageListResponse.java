package com.ccs.ipc.dto.patientdto;

import lombok.Data;
import java.util.List;

@Data
public class ConsultationMessageListResponse {
    private List<ConsultationMessageResponse> records;
    private Long total;
    private Long current;
    private Long size;
}

