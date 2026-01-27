package com.ccs.ipc.dto.patientdto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateConsultationSessionRequest {
    @NotNull(message = "会话类型不能为空")
    private Byte sessionType; // 1-AI问诊 2-医生问诊
    
    private String title;
    
    private Long doctorId; // 医生问诊时需要
}

