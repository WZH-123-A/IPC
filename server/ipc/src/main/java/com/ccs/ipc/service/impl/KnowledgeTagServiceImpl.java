package com.ccs.ipc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ccs.ipc.dto.admindto.*;
import com.ccs.ipc.entity.KnowledgeTag;
import com.ccs.ipc.mapper.KnowledgeTagMapper;
import com.ccs.ipc.service.IKnowledgeTagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 知识库标签表 服务实现类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
@Service
public class KnowledgeTagServiceImpl extends ServiceImpl<KnowledgeTagMapper, KnowledgeTag> implements IKnowledgeTagService {

    @Override
    public KnowledgeTagListResponse getAdminTagList(KnowledgeTagListRequest request) {
        long current = request.getCurrent() != null && request.getCurrent() > 0 ? request.getCurrent() : 1L;
        long size = request.getSize() != null && request.getSize() > 0 ? request.getSize() : 10L;
        LambdaQueryWrapper<KnowledgeTag> qw = new LambdaQueryWrapper<>();
        qw.eq(KnowledgeTag::getIsDeleted, 0);
        if (StringUtils.hasText(request.getTagName())) {
            qw.like(KnowledgeTag::getTagName, request.getTagName());
        }
        qw.orderByAsc(KnowledgeTag::getId);
        Page<KnowledgeTag> page = new Page<>(current, size);
        Page<KnowledgeTag> result = this.page(page, qw);
        List<KnowledgeTagResponse> records = result.getRecords().stream().map(this::toResponse).collect(Collectors.toList());
        KnowledgeTagListResponse resp = new KnowledgeTagListResponse();
        resp.setRecords(records);
        resp.setTotal(result.getTotal());
        resp.setCurrent(result.getCurrent());
        resp.setSize(result.getSize());
        return resp;
    }

    @Override
    public KnowledgeTagResponse getAdminTagById(Long id) {
        KnowledgeTag t = this.getById(id);
        if (t == null || t.getIsDeleted() == 1) return null;
        return toResponse(t);
    }

    @Override
    public KnowledgeTagResponse createAdminTag(KnowledgeTagCreateRequest request) {
        KnowledgeTag t = new KnowledgeTag();
        t.setTagName(request.getTagName());
        t.setTagColor(request.getTagColor());
        t.setUseCount(0);
        t.setIsDeleted((byte) 0);
        this.save(t);
        return toResponse(t);
    }

    @Override
    public void updateAdminTag(Long id, KnowledgeTagUpdateRequest request) {
        KnowledgeTag t = this.getById(id);
        if (t == null || t.getIsDeleted() == 1) throw new RuntimeException("标签不存在");
        if (request.getTagName() != null) t.setTagName(request.getTagName());
        if (request.getTagColor() != null) t.setTagColor(request.getTagColor());
        this.updateById(t);
    }

    @Override
    public void deleteAdminTag(Long id) {
        KnowledgeTag t = this.getById(id);
        if (t == null || t.getIsDeleted() == 1) throw new RuntimeException("标签不存在");
        t.setIsDeleted((byte) 1);
        this.updateById(t);
    }

    private KnowledgeTagResponse toResponse(KnowledgeTag t) {
        KnowledgeTagResponse r = new KnowledgeTagResponse();
        BeanUtils.copyProperties(t, r);
        return r;
    }
}
