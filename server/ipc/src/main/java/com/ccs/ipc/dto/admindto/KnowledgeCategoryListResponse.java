package com.ccs.ipc.dto.admindto;

import lombok.Data;

import java.util.List;

@Data
public class KnowledgeCategoryListResponse {
    private List<KnowledgeCategoryResponse> records;
    private Long total;
    private Long current;
    private Long size;
}
