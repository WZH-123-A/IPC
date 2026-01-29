package com.ccs.ipc.dto.patientdto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 知识库内容列表项（患者端）
 *
 * @author WZH
 * @since 2026-01-29
 */
@Data
public class KnowledgeContentItem {

    private Long id;
    private Long categoryId;
    private String categoryName;
    private String title;
    private String subtitle;
    private Byte contentType; // 1-图文 2-视频 3-音频 4-文档
    private String coverImage;
    private String source;
    private String author;
    private Integer viewCount;
    private Integer likeCount;
    private LocalDateTime publishTime;
    /** 关联的标签列表 */
    private List<KnowledgeTagSimple> tags;
}
