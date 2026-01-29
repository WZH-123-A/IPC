package com.ccs.ipc.dto.patientdto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 知识库内容详情（患者端）
 *
 * @author WZH
 * @since 2026-01-29
 */
@Data
public class KnowledgeContentDetail {

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
    private LocalDateTime publishTime;
    private LocalDateTime createTime;
}
