package com.ccs.ipc.dto.admindto;

import lombok.Data;

@Data
public class KnowledgeCategoryListRequest {
    private Integer current = 1;
    private Integer size = 10;
    private String categoryName;
    private String categoryCode;
}
