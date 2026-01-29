package com.ccs.ipc.dto.admindto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KnowledgeCategoryResponse {
    private Long id;
    private String categoryCode;
    private String categoryName;
    private Long parentId;
    private Integer sort;
    private String icon;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
