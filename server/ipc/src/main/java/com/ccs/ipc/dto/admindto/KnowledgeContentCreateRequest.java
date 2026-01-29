package com.ccs.ipc.dto.admindto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class KnowledgeContentCreateRequest {
    @NotNull(message = "分类不能为空")
    private Long categoryId;
    @NotNull(message = "标题不能为空")
    private String title;
    private String subtitle;
    @NotNull(message = "内容类型不能为空")
    private Byte contentType; // 1-图文 2-视频 3-音频 4-文档
    private String coverImage;
    private String content;
    private String videoUrl;
    private String audioUrl;
    private String documentUrl;
    private String source;
    private String author;
    private Byte status = 1; // 0-草稿 1-已发布 2-已下架
}
