package com.ccs.ipc.dto.patientdto;

import lombok.Data;

/**
 * 知识库标签简要（患者端展示）
 *
 * @author WZH
 * @since 2026-01-29
 */
@Data
public class KnowledgeTagSimple {

    private Long id;
    private String tagName;
    private String tagColor;
}
