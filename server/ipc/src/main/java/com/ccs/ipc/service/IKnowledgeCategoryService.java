package com.ccs.ipc.service;

import com.ccs.ipc.dto.patientdto.KnowledgeCategorySimple;
import com.ccs.ipc.entity.KnowledgeCategory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 知识库分类表 服务类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
public interface IKnowledgeCategoryService extends IService<KnowledgeCategory> {

    /**
     * 患者端：获取顶级分类列表（已发布、未删除）
     */
    List<KnowledgeCategorySimple> listForPatient();
}
