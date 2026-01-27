package com.ccs.ipc.dto.patientdto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DiagnosisResultResponse {
    private Long id;
    private Long recordId;
    private Long diseaseId;
    private String diseaseName;
    private BigDecimal confidence;
    private Integer rank;
    private LocalDateTime createTime;
}

