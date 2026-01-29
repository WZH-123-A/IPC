package com.ccs.ipc.dto.admindto;

import lombok.Data;

import java.util.List;

@Data
public class KnowledgeTagListResponse {
    private List<KnowledgeTagResponse> records;
    private Long total;
    private Long current;
    private Long size;
}
