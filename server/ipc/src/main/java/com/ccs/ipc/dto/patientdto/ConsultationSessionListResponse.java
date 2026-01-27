package com.ccs.ipc.dto.patientdto;

import lombok.Data;
import java.util.List;

@Data
public class ConsultationSessionListResponse {
    private List<ConsultationSessionResponse> records;
    private Long total;
    private Long current;
    private Long size;
}

