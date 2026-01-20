package com.ccs.ipc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 知识库内容表
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("knowledge_content")
public class KnowledgeContent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 内容ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 标题
     */
    private String title;

    /**
     * 副标题
     */
    private String subtitle;

    /**
     * 内容类型：1-图文 2-视频 3-音频 4-文档
     */
    private Byte contentType;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 正文内容（富文本）
     */
    private String content;

    /**
     * 视频URL
     */
    private String videoUrl;

    /**
     * 音频URL
     */
    private String audioUrl;

    /**
     * 文档URL
     */
    private String documentUrl;

    /**
     * 来源（权威机构）
     */
    private String source;

    /**
     * 作者
     */
    private String author;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 分享数
     */
    private Integer shareCount;

    /**
     * 状态：0-草稿 1-已发布 2-已下架
     */
    private Byte status;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 创建人ID
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除：0-否 1-是
     */
    private Byte isDeleted;
}
