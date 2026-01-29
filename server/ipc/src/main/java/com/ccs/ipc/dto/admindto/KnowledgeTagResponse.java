package com.ccs.ipc.dto.admindto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KnowledgeTagResponse {
    private Long id;
    private String tagName;
    private String tagColor;
    private Integer useCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
