package com.ccs.ipc.dto.patientdto;

import lombok.Data;

@Data
public class DiseaseTypeResponse {
    private Long id;
    private String name;
    private String description;
    private String category;
}

