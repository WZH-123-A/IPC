package com.ccs.ipc.dto.doctordto;

import lombok.Data;

/**
 * 患者列表查询请求DTO
 *
 * @author WZH
 * @since 2026-01-27
 */
@Data
public class PatientListRequest {
    /**
     * 当前页码
     */
    private Integer current = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;

    /**
     * 患者姓名（模糊查询）
     */
    private String realName;

    /**
     * 患者用户名（模糊查询）
     */
    private String username;

    /**
     * 手机号（模糊查询）
     */
    private String phone;
}
