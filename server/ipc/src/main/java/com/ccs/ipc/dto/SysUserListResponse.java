package com.ccs.ipc.dto;

import lombok.Data;

import java.util.List;

@Data
public class SysUserListResponse {
    private List<SysUserResponse> records;

    private Long total;

    private Long current;

    private Long size;
}
