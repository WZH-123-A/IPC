package com.ccs.ipc.dto.admindto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class KnowledgeContentResponse {
    private Long id;
    private Long categoryId;
    private String categoryName;
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
    private Integer viewCount;
    private Integer likeCount;
    private Integer shareCount;
    private Byte status;
    private LocalDateTime publishTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    /** 关联的标签ID列表（内容-标签关联） */
    private List<Long> tagIds;
}
