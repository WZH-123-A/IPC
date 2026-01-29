package com.ccs.ipc.dto.admindto;

import lombok.Data;

import java.util.List;

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
    /** 关联的标签ID列表（内容-标签关联，传空列表表示清空） */
    private List<Long> tagIds;
}
