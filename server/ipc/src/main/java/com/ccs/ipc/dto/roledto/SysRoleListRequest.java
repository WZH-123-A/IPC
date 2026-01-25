package com.ccs.ipc.dto.roledto;

import lombok.Data;

@Data
public class SysRoleListRequest {
    private Integer current = 1;
    private Integer size = 10;
    private String roleCode;
    private String roleName;
}
