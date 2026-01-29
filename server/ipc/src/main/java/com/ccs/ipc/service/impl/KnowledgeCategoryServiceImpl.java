package com.ccs.ipc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ccs.ipc.dto.admindto.*;
import com.ccs.ipc.dto.patientdto.KnowledgeCategorySimple;
import com.ccs.ipc.entity.KnowledgeCategory;
import com.ccs.ipc.mapper.KnowledgeCategoryMapper;
import com.ccs.ipc.service.IKnowledgeCategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 知识库分类表 服务实现类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
@Service
public class KnowledgeCategoryServiceImpl extends ServiceImpl<KnowledgeCategoryMapper, KnowledgeCategory> implements IKnowledgeCategoryService {

    @Override
    public List<KnowledgeCategorySimple> listForPatient() {
        LambdaQueryWrapper<KnowledgeCategory> qw = new LambdaQueryWrapper<>();
        qw.eq(KnowledgeCategory::getIsDeleted, 0)
                .and(w -> w.eq(KnowledgeCategory::getParentId, 0).or().isNull(KnowledgeCategory::getParentId))
                .orderByAsc(KnowledgeCategory::getSort);
        List<KnowledgeCategory> list = this.list(qw);
        return list.stream().map(c -> {
            KnowledgeCategorySimple dto = new KnowledgeCategorySimple();
            BeanUtils.copyProperties(c, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public KnowledgeCategoryListResponse getAdminCategoryList(KnowledgeCategoryListRequest request) {
        long current = request.getCurrent() != null && request.getCurrent() > 0 ? request.getCurrent() : 1L;
        long size = request.getSize() != null && request.getSize() > 0 ? request.getSize() : 10L;
        LambdaQueryWrapper<KnowledgeCategory> qw = new LambdaQueryWrapper<>();
        qw.eq(KnowledgeCategory::getIsDeleted, 0);
        if (StringUtils.hasText(request.getCategoryName())) {
            qw.like(KnowledgeCategory::getCategoryName, request.getCategoryName());
        }
        if (StringUtils.hasText(request.getCategoryCode())) {
            qw.like(KnowledgeCategory::getCategoryCode, request.getCategoryCode());
        }
        qw.orderByAsc(KnowledgeCategory::getSort).orderByAsc(KnowledgeCategory::getId);
        Page<KnowledgeCategory> page = new Page<>(current, size);
        Page<KnowledgeCategory> result = this.page(page, qw);
        List<KnowledgeCategoryResponse> records = result.getRecords().stream()
                .map(this::toResponse).collect(Collectors.toList());
        KnowledgeCategoryListResponse resp = new KnowledgeCategoryListResponse();
        resp.setRecords(records);
        resp.setTotal(result.getTotal());
        resp.setCurrent(result.getCurrent());
        resp.setSize(result.getSize());
        return resp;
    }

    @Override
    public KnowledgeCategoryResponse getAdminCategoryById(Long id) {
        KnowledgeCategory c = this.getById(id);
        if (c == null || c.getIsDeleted() == 1) return null;
        return toResponse(c);
    }

    @Override
    public KnowledgeCategoryResponse createAdminCategory(KnowledgeCategoryCreateRequest request) {
        KnowledgeCategory c = new KnowledgeCategory();
        c.setCategoryCode(request.getCategoryCode());
        c.setCategoryName(request.getCategoryName());
        c.setParentId(request.getParentId() != null ? request.getParentId() : 0L);
        c.setSort(request.getSort() != null ? request.getSort() : 0);
        c.setIcon(request.getIcon());
        c.setDescription(request.getDescription());
        c.setIsDeleted((byte) 0);
        this.save(c);
        return toResponse(c);
    }

    @Override
    public void updateAdminCategory(Long id, KnowledgeCategoryUpdateRequest request) {
        KnowledgeCategory c = this.getById(id);
        if (c == null || c.getIsDeleted() == 1) throw new RuntimeException("分类不存在");
        if (request.getCategoryName() != null) c.setCategoryName(request.getCategoryName());
        if (request.getParentId() != null) c.setParentId(request.getParentId());
        if (request.getSort() != null) c.setSort(request.getSort());
        if (request.getIcon() != null) c.setIcon(request.getIcon());
        if (request.getDescription() != null) c.setDescription(request.getDescription());
        this.updateById(c);
    }

    @Override
    public void deleteAdminCategory(Long id) {
        KnowledgeCategory c = this.getById(id);
        if (c == null || c.getIsDeleted() == 1) throw new RuntimeException("分类不存在");
        c.setIsDeleted((byte) 1);
        this.updateById(c);
    }

    private KnowledgeCategoryResponse toResponse(KnowledgeCategory c) {
        KnowledgeCategoryResponse r = new KnowledgeCategoryResponse();
        BeanUtils.copyProperties(c, r);
        return r;
    }
}
