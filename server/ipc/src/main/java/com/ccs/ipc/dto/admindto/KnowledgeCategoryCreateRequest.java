package com.ccs.ipc.dto.admindto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class KnowledgeCategoryCreateRequest {
    @NotBlank(message = "分类编码不能为空")
    private String categoryCode;
    @NotBlank(message = "分类名称不能为空")
    private String categoryName;
    private Long parentId = 0L;
    private Integer sort = 0;
    private String icon;
    private String description;
}
