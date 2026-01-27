package com.ccs.ipc.dto.patientdto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DiagnosisRecordResponse {
    private Long id;
    private Long userId;
    private String imageUrl;
    private String imageName;
    private String bodyPart;
    private String modelVersion;
    private Byte status; // 0-识别中 1-识别成功 2-识别失败
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

