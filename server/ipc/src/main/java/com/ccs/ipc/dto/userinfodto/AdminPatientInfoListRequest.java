package com.ccs.ipc.dto.userinfodto;

import lombok.Data;

/**
 * 管理员-患者信息列表查询请求
 *
 * @author WZH
 * @since 2026-01-29
 */
@Data
public class AdminPatientInfoListRequest {

    private Integer current = 1;
    private Integer size = 10;

    /** 用户名（关联 sys_user，模糊） */
    private String username;
    /** 真实姓名（关联 sys_user，模糊） */
    private String realName;
    /** 年龄 */
    private Integer age;
}
