package com.ccs.ipc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ccs.ipc.dto.patientdto.KnowledgeCategorySimple;
import com.ccs.ipc.entity.KnowledgeCategory;
import com.ccs.ipc.mapper.KnowledgeCategoryMapper;
import com.ccs.ipc.service.IKnowledgeCategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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
}
