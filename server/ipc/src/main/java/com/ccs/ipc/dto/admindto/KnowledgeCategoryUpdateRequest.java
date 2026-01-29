package com.ccs.ipc.dto.admindto;

import lombok.Data;

@Data
public class KnowledgeCategoryUpdateRequest {
    private String categoryName;
    private Long parentId;
    private Integer sort;
    private String icon;
    private String description;
}
