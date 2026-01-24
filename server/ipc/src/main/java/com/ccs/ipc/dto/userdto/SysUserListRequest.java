package com.ccs.ipc.dto.userdto;

import lombok.Data;

@Data
public class SysUserListRequest {
    private Integer current =1;

    private Integer size = 10;

    private String userName;

    private String realName;

    private Byte status;
}
