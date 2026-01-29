package com.ccs.ipc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ccs.ipc.dto.patientdto.KnowledgeContentDetail;
import com.ccs.ipc.dto.patientdto.KnowledgeContentItem;
import com.ccs.ipc.dto.patientdto.KnowledgeContentListRequest;
import com.ccs.ipc.dto.patientdto.KnowledgeContentListResponse;
import com.ccs.ipc.entity.KnowledgeCategory;
import com.ccs.ipc.entity.KnowledgeContent;
import com.ccs.ipc.mapper.KnowledgeContentMapper;
import com.ccs.ipc.service.IKnowledgeCategoryService;
import com.ccs.ipc.service.IKnowledgeContentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 知识库内容表 服务实现类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
@Service
public class KnowledgeContentServiceImpl extends ServiceImpl<KnowledgeContentMapper, KnowledgeContent> implements IKnowledgeContentService {

    @Autowired
    private IKnowledgeCategoryService knowledgeCategoryService;

    @Override
    public KnowledgeContentListResponse listForPatient(KnowledgeContentListRequest request) {
        long current = request.getCurrent() != null && request.getCurrent() > 0 ? request.getCurrent() : 1L;
        long size = request.getSize() != null && request.getSize() > 0 ? request.getSize() : 12L;

        LambdaQueryWrapper<KnowledgeContent> qw = new LambdaQueryWrapper<>();
        qw.eq(KnowledgeContent::getIsDeleted, 0)
                .eq(KnowledgeContent::getStatus, (byte) 1); // 1-已发布
        if (request.getCategoryId() != null) {
            qw.eq(KnowledgeContent::getCategoryId, request.getCategoryId());
        }
        if (StringUtils.hasText(request.getKeyword())) {
            qw.and(w -> w.like(KnowledgeContent::getTitle, request.getKeyword())
                    .or().like(KnowledgeContent::getSubtitle, request.getKeyword()));
        }
        qw.orderByDesc(KnowledgeContent::getPublishTime)
                .orderByDesc(KnowledgeContent::getCreateTime);

        Page<KnowledgeContent> page = new Page<>(current, size);
        Page<KnowledgeContent> result = this.page(page, qw);
        List<KnowledgeContent> records = result.getRecords();
        if (records.isEmpty()) {
            KnowledgeContentListResponse resp = new KnowledgeContentListResponse();
            resp.setRecords(List.of());
            resp.setTotal(0L);
            resp.setCurrent(current);
            resp.setSize(size);
            return resp;
        }

        Set<Long> categoryIds = records.stream().map(KnowledgeContent::getCategoryId).collect(Collectors.toSet());
        Map<Long, KnowledgeCategory> categoryMap = knowledgeCategoryService.listByIds(categoryIds).stream()
                .collect(Collectors.toMap(KnowledgeCategory::getId, c -> c));

        List<KnowledgeContentItem> items = records.stream().map(c -> {
            KnowledgeContentItem item = new KnowledgeContentItem();
            item.setId(c.getId());
            item.setCategoryId(c.getCategoryId());
            KnowledgeCategory cat = categoryMap.get(c.getCategoryId());
            item.setCategoryName(cat != null ? cat.getCategoryName() : null);
            item.setTitle(c.getTitle());
            item.setSubtitle(c.getSubtitle());
            item.setContentType(c.getContentType());
            item.setCoverImage(c.getCoverImage());
            item.setSource(c.getSource());
            item.setAuthor(c.getAuthor());
            item.setViewCount(c.getViewCount());
            item.setLikeCount(c.getLikeCount());
            item.setPublishTime(c.getPublishTime());
            return item;
        }).collect(Collectors.toList());

        KnowledgeContentListResponse resp = new KnowledgeContentListResponse();
        resp.setRecords(items);
        resp.setTotal(result.getTotal());
        resp.setCurrent(result.getCurrent());
        resp.setSize(result.getSize());
        return resp;
    }

    @Override
    public KnowledgeContentDetail getDetailForPatient(Long id) {
        KnowledgeContent c = this.getById(id);
        if (c == null || c.getIsDeleted() == 1 || c.getStatus() == null || c.getStatus() != 1) {
            return null;
        }
        // 增加浏览次数
        if (c.getViewCount() == null) {
            c.setViewCount(0);
        }
        c.setViewCount(c.getViewCount() + 1);
        this.updateById(c);

        KnowledgeContentDetail detail = new KnowledgeContentDetail();
        detail.setId(c.getId());
        detail.setCategoryId(c.getCategoryId());
        KnowledgeCategory cat = knowledgeCategoryService.getById(c.getCategoryId());
        detail.setCategoryName(cat != null ? cat.getCategoryName() : null);
        detail.setTitle(c.getTitle());
        detail.setSubtitle(c.getSubtitle());
        detail.setContentType(c.getContentType());
        detail.setCoverImage(c.getCoverImage());
        detail.setContent(c.getContent());
        detail.setVideoUrl(c.getVideoUrl());
        detail.setAudioUrl(c.getAudioUrl());
        detail.setDocumentUrl(c.getDocumentUrl());
        detail.setSource(c.getSource());
        detail.setAuthor(c.getAuthor());
        detail.setViewCount(c.getViewCount());
        detail.setLikeCount(c.getLikeCount());
        detail.setShareCount(c.getShareCount());
        detail.setPublishTime(c.getPublishTime());
        detail.setCreateTime(c.getCreateTime());
        return detail;
    }
}
