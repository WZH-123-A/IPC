package com.ccs.ipc.dto.patientdto;

import lombok.Data;

/**
 * 知识库分类（患者端简要）
 *
 * @author WZH
 * @since 2026-01-29
 */
@Data
public class KnowledgeCategorySimple {

    private Long id;
    private String categoryCode;
    private String categoryName;
    private String icon;
    private String description;
    private Integer sort;
}
