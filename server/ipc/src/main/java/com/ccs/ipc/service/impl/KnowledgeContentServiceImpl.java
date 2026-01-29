package com.ccs.ipc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ccs.ipc.dto.admindto.KnowledgeContentCreateRequest;
import com.ccs.ipc.dto.admindto.AdminKnowledgeContentListRequest;
import com.ccs.ipc.dto.admindto.AdminKnowledgeContentListResponse;
import com.ccs.ipc.dto.admindto.KnowledgeContentResponse;
import com.ccs.ipc.dto.admindto.KnowledgeContentUpdateRequest;
import com.ccs.ipc.dto.patientdto.KnowledgeContentDetail;
import com.ccs.ipc.dto.patientdto.KnowledgeContentItem;
import com.ccs.ipc.dto.patientdto.KnowledgeContentListRequest;
import com.ccs.ipc.dto.patientdto.KnowledgeContentListResponse;
import com.ccs.ipc.dto.patientdto.KnowledgeTagSimple;
import com.ccs.ipc.entity.KnowledgeCategory;
import com.ccs.ipc.entity.KnowledgeContent;
import com.ccs.ipc.entity.KnowledgeContentTag;
import com.ccs.ipc.entity.KnowledgeTag;
import com.ccs.ipc.mapper.KnowledgeContentMapper;
import com.ccs.ipc.service.IKnowledgeCategoryService;
import com.ccs.ipc.service.IKnowledgeContentService;
import com.ccs.ipc.service.IKnowledgeContentTagService;
import com.ccs.ipc.service.IKnowledgeTagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
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

    @Autowired
    private IKnowledgeContentTagService knowledgeContentTagService;

    @Autowired
    private IKnowledgeTagService knowledgeTagService;

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

        Set<Long> contentIds = records.stream().map(KnowledgeContent::getId).collect(Collectors.toSet());
        Map<Long, List<KnowledgeTagSimple>> contentIdToTags = buildContentIdToTags(contentIds);

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
            item.setTags(contentIdToTags.getOrDefault(c.getId(), Collections.emptyList()));
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
    public AdminKnowledgeContentListResponse getAdminContentList(AdminKnowledgeContentListRequest request) {
        long current = request.getCurrent() != null && request.getCurrent() > 0 ? request.getCurrent() : 1L;
        long size = request.getSize() != null && request.getSize() > 0 ? request.getSize() : 10L;
        LambdaQueryWrapper<KnowledgeContent> qw = new LambdaQueryWrapper<>();
        qw.eq(KnowledgeContent::getIsDeleted, 0);
        if (request.getCategoryId() != null) {
            qw.eq(KnowledgeContent::getCategoryId, request.getCategoryId());
        }
        if (StringUtils.hasText(request.getTitle())) {
            qw.like(KnowledgeContent::getTitle, request.getTitle());
        }
        if (request.getStatus() != null) {
            qw.eq(KnowledgeContent::getStatus, request.getStatus());
        }
        qw.orderByDesc(KnowledgeContent::getCreateTime);
        Page<KnowledgeContent> page = new Page<>(current, size);
        Page<KnowledgeContent> result = this.page(page, qw);
        List<KnowledgeContent> records = result.getRecords();
        Set<Long> contentIds = records.stream().map(KnowledgeContent::getId).collect(Collectors.toSet());
        Set<Long> categoryIds = records.stream().map(KnowledgeContent::getCategoryId).collect(Collectors.toSet());
        Map<Long, KnowledgeCategory> categoryMap = categoryIds.isEmpty() ? Map.of() : knowledgeCategoryService.listByIds(categoryIds).stream().collect(Collectors.toMap(KnowledgeCategory::getId, c -> c));
        Map<Long, List<Long>> contentIdToTagIds = contentIds.isEmpty() ? Map.of() : knowledgeContentTagService.lambdaQuery()
                .in(KnowledgeContentTag::getContentId, contentIds)
                .list().stream()
                .collect(Collectors.groupingBy(KnowledgeContentTag::getContentId, Collectors.mapping(KnowledgeContentTag::getTagId, Collectors.toList())));
        List<KnowledgeContentResponse> list = records.stream().map(c -> {
            KnowledgeContentResponse r = toAdminResponse(c, categoryMap.get(c.getCategoryId()));
            r.setTagIds(contentIdToTagIds.getOrDefault(c.getId(), Collections.emptyList()));
            return r;
        }).collect(Collectors.toList());
        AdminKnowledgeContentListResponse resp = new AdminKnowledgeContentListResponse();
        resp.setRecords(list);
        resp.setTotal(result.getTotal());
        resp.setCurrent(result.getCurrent());
        resp.setSize(result.getSize());
        return resp;
    }

    @Override
    public KnowledgeContentResponse getAdminContentById(Long id) {
        KnowledgeContent c = this.getById(id);
        if (c == null || c.getIsDeleted() == 1) return null;
        KnowledgeCategory cat = knowledgeCategoryService.getById(c.getCategoryId());
        KnowledgeContentResponse r = toAdminResponse(c, cat);
        List<KnowledgeContentTag> ctList = knowledgeContentTagService.lambdaQuery()
                .eq(KnowledgeContentTag::getContentId, id)
                .list();
        r.setTagIds(ctList.stream().map(KnowledgeContentTag::getTagId).collect(Collectors.toList()));
        return r;
    }

    @Override
    public KnowledgeContentResponse createAdminContent(KnowledgeContentCreateRequest request, Long createBy) {
        KnowledgeContent c = new KnowledgeContent();
        c.setCategoryId(request.getCategoryId());
        c.setTitle(request.getTitle());
        c.setSubtitle(request.getSubtitle());
        c.setContentType(request.getContentType());
        c.setCoverImage(request.getCoverImage());
        c.setContent(request.getContent());
        c.setVideoUrl(request.getVideoUrl());
        c.setAudioUrl(request.getAudioUrl());
        c.setDocumentUrl(request.getDocumentUrl());
        c.setSource(request.getSource());
        c.setAuthor(request.getAuthor());
        c.setViewCount(0);
        c.setLikeCount(0);
        c.setShareCount(0);
        c.setStatus(request.getStatus() != null ? request.getStatus() : (byte) 1);
        c.setPublishTime(c.getStatus() == 1 ? java.time.LocalDateTime.now() : null);
        c.setCreateBy(createBy);
        c.setIsDeleted((byte) 0);
        this.save(c);
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            for (Long tagId : request.getTagIds()) {
                KnowledgeContentTag ct = new KnowledgeContentTag();
                ct.setContentId(c.getId());
                ct.setTagId(tagId);
                knowledgeContentTagService.save(ct);
            }
        }
        KnowledgeCategory cat = knowledgeCategoryService.getById(c.getCategoryId());
        return toAdminResponse(c, cat);
    }

    @Override
    public void updateAdminContent(Long id, KnowledgeContentUpdateRequest request) {
        KnowledgeContent c = this.getById(id);
        if (c == null || c.getIsDeleted() == 1) throw new RuntimeException("内容不存在");
        if (request.getCategoryId() != null) c.setCategoryId(request.getCategoryId());
        if (request.getTitle() != null) c.setTitle(request.getTitle());
        if (request.getSubtitle() != null) c.setSubtitle(request.getSubtitle());
        if (request.getContentType() != null) c.setContentType(request.getContentType());
        if (request.getCoverImage() != null) c.setCoverImage(request.getCoverImage());
        if (request.getContent() != null) c.setContent(request.getContent());
        if (request.getVideoUrl() != null) c.setVideoUrl(request.getVideoUrl());
        if (request.getAudioUrl() != null) c.setAudioUrl(request.getAudioUrl());
        if (request.getDocumentUrl() != null) c.setDocumentUrl(request.getDocumentUrl());
        if (request.getSource() != null) c.setSource(request.getSource());
        if (request.getAuthor() != null) c.setAuthor(request.getAuthor());
        if (request.getStatus() != null) {
            c.setStatus(request.getStatus());
            if (request.getStatus() == 1 && c.getPublishTime() == null) {
                c.setPublishTime(java.time.LocalDateTime.now());
            }
        }
        this.updateById(c);
        if (request.getTagIds() != null) {
            knowledgeContentTagService.lambdaUpdate()
                    .eq(KnowledgeContentTag::getContentId, id)
                    .remove();
            for (Long tagId : request.getTagIds()) {
                KnowledgeContentTag ct = new KnowledgeContentTag();
                ct.setContentId(id);
                ct.setTagId(tagId);
                knowledgeContentTagService.save(ct);
            }
        }
    }

    @Override
    public void deleteAdminContent(Long id) {
        KnowledgeContent c = this.getById(id);
        if (c == null || c.getIsDeleted() == 1) throw new RuntimeException("内容不存在");
        c.setIsDeleted((byte) 1);
        this.updateById(c);
    }

    private KnowledgeContentResponse toAdminResponse(KnowledgeContent c, KnowledgeCategory cat) {
        KnowledgeContentResponse r = new KnowledgeContentResponse();
        BeanUtils.copyProperties(c, r);
        r.setCategoryName(cat != null ? cat.getCategoryName() : null);
        return r;
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
        List<KnowledgeTagSimple> detailTags = buildContentIdToTags(Collections.singleton(id)).getOrDefault(id, Collections.emptyList());
        detail.setTags(detailTags);
        return detail;
    }

    /**
     * 按内容ID批量查询关联的标签（患者端展示用）
     */
    private Map<Long, List<KnowledgeTagSimple>> buildContentIdToTags(Set<Long> contentIds) {
        if (contentIds == null || contentIds.isEmpty()) {
            return Map.of();
        }
        List<KnowledgeContentTag> ctList = knowledgeContentTagService.lambdaQuery()
                .in(KnowledgeContentTag::getContentId, contentIds)
                .list();
        if (ctList.isEmpty()) {
            return Map.of();
        }
        Set<Long> tagIds = ctList.stream().map(KnowledgeContentTag::getTagId).collect(Collectors.toSet());
        List<KnowledgeTag> tags = knowledgeTagService.listByIds(tagIds);
        Map<Long, KnowledgeTag> tagMap = tags.stream().collect(Collectors.toMap(KnowledgeTag::getId, t -> t));
        Map<Long, List<Long>> contentIdToTagIds = ctList.stream()
                .collect(Collectors.groupingBy(KnowledgeContentTag::getContentId,
                        Collectors.mapping(KnowledgeContentTag::getTagId, Collectors.toList())));
        Map<Long, List<KnowledgeTagSimple>> result = new java.util.HashMap<>();
        for (Long contentId : contentIds) {
            List<Long> tIds = contentIdToTagIds.getOrDefault(contentId, Collections.emptyList());
            List<KnowledgeTagSimple> simples = new ArrayList<>();
            for (Long tagId : tIds) {
                KnowledgeTag t = tagMap.get(tagId);
                if (t != null && (t.getIsDeleted() == null || t.getIsDeleted() == 0)) {
                    KnowledgeTagSimple s = new KnowledgeTagSimple();
                    s.setId(t.getId());
                    s.setTagName(t.getTagName());
                    s.setTagColor(t.getTagColor());
                    simples.add(s);
                }
            }
            result.put(contentId, simples);
        }
        return result;
    }
}
