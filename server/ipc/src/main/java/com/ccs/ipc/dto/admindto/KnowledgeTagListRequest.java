package com.ccs.ipc.dto.admindto;

import lombok.Data;

@Data
public class KnowledgeTagListRequest {
    private Integer current = 1;
    private Integer size = 10;
    private String tagName;
}
