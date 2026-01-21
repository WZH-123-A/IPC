package com.ccs.ipc.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统一响应结果封装类
 *
 * @param <T> 响应数据类型
 * @author WZH
 * @since 2026-01-19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 成功响应（无数据）
     *
     * @param <T> 数据类型
     * @return Response
     */
    public static <T> Response<T> success() {
        return success(null);
    }

    /**
     * 成功响应（带数据）
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return Response
     */
    public static <T> Response<T> success(T data) {
        Response<T> response = new Response<>();
        response.setCode(ResultCode.SUCCESS.getCode());
        response.setMessage(ResultCode.SUCCESS.getMessage());
        response.setData(data);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

    /**
     * 失败响应
     *
     * @param <T> 数据类型
     * @return Response
     */
    public static <T> Response<T> fail() {
        return fail(ResultCode.FAIL, null);
    }

    /**
     * 失败响应（使用 ResultCode）
     *
     * @param resultCode 结果码
     * @param <T>        数据类型
     * @return Response
     */
    public static <T> Response<T> fail(ResultCode resultCode, T data) {
        Response<T> response = new Response<>();
        response.setCode(resultCode.getCode());
        response.setMessage(resultCode.getMessage());
        response.setData(data);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

    /**
     * 失败响应（自定义消息）
     *
     * @param message 错误消息
     * @param <T>      数据类型
     * @return Response
     */
    public static <T> Response<T> fail(String message) {
        Response<T> response = new Response<>();

        Integer failCode;
        if (ResultCode.UNAUTHORIZED.getMessage().equals(message)) {
            failCode = ResultCode.UNAUTHORIZED.getCode();
        } else if (ResultCode.TOKEN_INVALID.getMessage().equals(message)) {
            failCode = ResultCode.TOKEN_INVALID.getCode();
        } else if (ResultCode.TOKEN_EXPIRED.getMessage().equals(message)) {
            failCode = ResultCode.TOKEN_EXPIRED.getCode();
        } else if (ResultCode.USER_NOT_FOUND.getMessage().equals(message)) {
            failCode = ResultCode.USER_NOT_FOUND.getCode();
        } else if (ResultCode.USER_PASSWORD_ERROR.getMessage().equals(message)) {
            failCode = ResultCode.USER_PASSWORD_ERROR.getCode();
        } else if (ResultCode.USER_DISABLED.getMessage().equals(message)) {
            failCode = ResultCode.USER_DISABLED.getCode();
        } else {
            failCode = ResultCode.FAIL.getCode();
        }
        response.setCode(failCode);
        response.setMessage(message);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }
}
