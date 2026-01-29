package com.ccs.ipc.dto.admindto;

import lombok.Data;

import java.util.List;

@Data
public class AdminKnowledgeContentListResponse {
    private List<KnowledgeContentResponse> records;
    private Long total;
    private Long current;
    private Long size;
}
