package com.ccs.ipc.dto.patientdto;

import lombok.Data;
import java.util.List;

@Data
public class DiagnosisRecordListResponse {
    private List<DiagnosisRecordResponse> records;
    private Long total;
    private Long current;
    private Long size;
}

