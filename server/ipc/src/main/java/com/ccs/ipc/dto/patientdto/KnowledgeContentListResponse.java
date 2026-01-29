package com.ccs.ipc.dto.patientdto;

import lombok.Data;

import java.util.List;

/**
 * 知识库内容列表响应（患者端）
 *
 * @author WZH
 * @since 2026-01-29
 */
@Data
public class KnowledgeContentListResponse {

    private List<KnowledgeContentItem> records;
    private Long total;
    private Long current;
    private Long size;
}
