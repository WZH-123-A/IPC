package com.ccs.ipc.dto.patientdto;

import lombok.Data;

/**
 * 知识库内容列表请求（患者端）
 *
 * @author WZH
 * @since 2026-01-29
 */
@Data
public class KnowledgeContentListRequest {

    private Integer current = 1;
    private Integer size = 12;
    private Long categoryId;
    private String keyword;
}
