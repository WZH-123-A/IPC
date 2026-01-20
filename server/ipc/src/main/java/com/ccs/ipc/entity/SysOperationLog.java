package com.ccs.ipc.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 系统操作日志表
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_operation_log")
public class SysOperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 操作类型（如：登录、查询、新增、修改、删除）
     */
    private String operationType;

    /**
     * 操作模块
     */
    private String operationModule;

    /**
     * 操作描述
     */
    private String operationDesc;

    /**
     * 请求方法（GET/POST等）
     */
    private String requestMethod;

    /**
     * 请求URL
     */
    private String requestUrl;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 响应数据
     */
    private String responseData;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 操作状态：0-失败 1-成功
     */
    private Byte status;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 执行时间（毫秒）
     */
    private Long executionTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
