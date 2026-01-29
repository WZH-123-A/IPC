package com.ccs.ipc.service;

import com.ccs.ipc.dto.patientdto.KnowledgeContentDetail;
import com.ccs.ipc.dto.patientdto.KnowledgeContentItem;
import com.ccs.ipc.dto.patientdto.KnowledgeContentListRequest;
import com.ccs.ipc.dto.patientdto.KnowledgeContentListResponse;
import com.ccs.ipc.entity.KnowledgeContent;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 知识库内容表 服务类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
public interface IKnowledgeContentService extends IService<KnowledgeContent> {

    /**
     * 患者端：分页查询已发布内容
     */
    KnowledgeContentListResponse listForPatient(KnowledgeContentListRequest request);

    /**
     * 患者端：获取内容详情（并增加浏览次数）
     */
    KnowledgeContentDetail getDetailForPatient(Long id);
}
