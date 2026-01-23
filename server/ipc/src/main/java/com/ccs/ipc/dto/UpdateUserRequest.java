package com.ccs.ipc.dto;

import lombok.Data;

import java.util.List;

/**
 * 更新用户请求DTO
 * 用于用户自己更新信息（不包含status和roleIds）
 * 或管理员更新用户信息（包含所有字段）
 *
 * @author WZH
 * @since 2026-01-19
 */
@Data
public class UpdateUserRequest {
    private String phone;
    private String email;
    private String realName;
    private Byte gender;
    private String avatar;
    private Byte status;  // 仅管理员可更新
    private List<Long> roleIds;  // 仅管理员可更新
}
