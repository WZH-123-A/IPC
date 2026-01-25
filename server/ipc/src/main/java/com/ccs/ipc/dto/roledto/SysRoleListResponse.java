package com.ccs.ipc.dto.roledto;

import lombok.Data;

import java.util.List;

@Data
public class SysRoleListResponse {
    private List<SysRoleResponse> records;

    private Long total;

    private Long current;

    private Long size;
}
