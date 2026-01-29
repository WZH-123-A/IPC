package com.ccs.ipc.dto.admindto;

import lombok.Data;

@Data
public class KnowledgeContentUpdateRequest {
    private Long categoryId;
    private String title;
    private String subtitle;
    private Byte contentType;
    private String coverImage;
    private String content;
    private String videoUrl;
    private String audioUrl;
    private String documentUrl;
    private String source;
    private String author;
    private Byte status;
}
