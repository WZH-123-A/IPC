package com.ccs.ipc.service;

import com.ccs.ipc.dto.admindto.*;
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

    /**
     * 管理员：分页查询分类列表
     */
    KnowledgeCategoryListResponse getAdminCategoryList(KnowledgeCategoryListRequest request);

    /**
     * 管理员：根据ID获取分类详情
     */
    KnowledgeCategoryResponse getAdminCategoryById(Long id);

    /**
     * 管理员：新增分类
     */
    KnowledgeCategoryResponse createAdminCategory(KnowledgeCategoryCreateRequest request);

    /**
     * 管理员：更新分类
     */
    void updateAdminCategory(Long id, KnowledgeCategoryUpdateRequest request);

    /**
     * 管理员：逻辑删除分类
     */
    void deleteAdminCategory(Long id);
}
