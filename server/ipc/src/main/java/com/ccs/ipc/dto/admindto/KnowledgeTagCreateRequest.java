package com.ccs.ipc.dto.admindto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class KnowledgeTagCreateRequest {
    @NotBlank(message = "标签名称不能为空")
    private String tagName;
    private String tagColor;
}
