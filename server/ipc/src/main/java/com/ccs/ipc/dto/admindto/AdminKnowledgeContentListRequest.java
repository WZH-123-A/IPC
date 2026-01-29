package com.ccs.ipc.dto.admindto;

import lombok.Data;

@Data
public class AdminKnowledgeContentListRequest {
    private Integer current = 1;
    private Integer size = 10;
    private Long categoryId;
    private String title;
    private Byte status; // 0-草稿 1-已发布 2-已下架
}
