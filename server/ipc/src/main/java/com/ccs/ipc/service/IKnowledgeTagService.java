package com.ccs.ipc.service;

import com.ccs.ipc.dto.admindto.*;
import com.ccs.ipc.entity.KnowledgeTag;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 知识库标签表 服务类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
public interface IKnowledgeTagService extends IService<KnowledgeTag> {

    KnowledgeTagListResponse getAdminTagList(KnowledgeTagListRequest request);

    KnowledgeTagResponse getAdminTagById(Long id);

    KnowledgeTagResponse createAdminTag(KnowledgeTagCreateRequest request);

    void updateAdminTag(Long id, KnowledgeTagUpdateRequest request);

    void deleteAdminTag(Long id);
}
