package com.ccs.ipc.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

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
        failCode = getErrorCode(message);
        response.setCode(failCode);
        response.setMessage(message);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

    /**
     * 根据业务错误码获取对应的HTTP状态码
     *
     * @return HTTP状态码
     */
    public HttpStatus getHttpStatus() {
        if (code == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

        // 如果是标准的HTTP状态码（200, 400, 401, 403等），直接使用
        if (code == 200) {
            return HttpStatus.OK;
        } else if (code == 400) {
            return HttpStatus.BAD_REQUEST;
        } else if (code == 401) {
            return HttpStatus.UNAUTHORIZED;
        } else if (code == 403) {
            return HttpStatus.FORBIDDEN;
        } else if (code == 405) {
            return HttpStatus.METHOD_NOT_ALLOWED;
        } else if (code == 415) {
            return HttpStatus.UNSUPPORTED_MEDIA_TYPE;
        } else if (code == 500) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

        // 业务错误码映射规则
        if (code >= 1000 && code < 2000) {
            return HttpStatus.BAD_REQUEST;
        } else if (code >= 2000 && code < 3000) {
            if (code == ResultCode.USER_DISABLED.getCode()) {
                return HttpStatus.FORBIDDEN;
            }
            return HttpStatus.BAD_REQUEST;
        } else if (code >= 3000 && code < 4000) {
            // 权限模块错误
            if (code == ResultCode.TOKEN_INVALID.getCode() || 
                code == ResultCode.TOKEN_EXPIRED.getCode()) {
                return HttpStatus.UNAUTHORIZED;
            } else if (code == ResultCode.NO_PERMISSION.getCode()) {
                return HttpStatus.FORBIDDEN;
            }
            return HttpStatus.UNAUTHORIZED;
        }

        // 默认返回500
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    /**
     * 根据错误信息获得错误码
     *
     * @param message 错误信息
     * @return 错误码
     */
    public static Integer getErrorCode(String message) {
        if (message == null) {
            return ResultCode.FAIL.getCode();
        }
        
        switch (message) {
            case "操作成功":
                return ResultCode.SUCCESS.getCode();
            case "操作失败":
                return ResultCode.FAIL.getCode();
            case "请求参数错误":
                return ResultCode.BAD_REQUEST.getCode();
            case "请求方法不允许":
                return ResultCode.METHOD_NOT_ALLOWED.getCode();
            case "不支持的媒体类型":
                return ResultCode.UNSUPPORTED_MEDIA_TYPE.getCode();
            case "未授权":
                return ResultCode.UNAUTHORIZED.getCode();
            case "禁止访问":
                return ResultCode.FORBIDDEN.getCode();
            case "业务异常":
                return ResultCode.BUSINESS_ERROR.getCode();
            case "参数校验失败":
                return ResultCode.PARAM_VALIDATE_ERROR.getCode();
            case "数据不存在":
                return ResultCode.DATA_NOT_FOUND.getCode();
            case "数据已存在":
                return ResultCode.DATA_ALREADY_EXISTS.getCode();
            case "权限编码已存在":
                return ResultCode.PERMISSION_CODE_EXISTS.getCode();
            case "权限编码不存在":
                return ResultCode.PERMISSION_CODE_NOT_FOUNT.getCode();
            case "角色编码已存在":
                return ResultCode.ROLE_CODE_EXISTS.getCode();
            case "角色编码不存在":
                return ResultCode.ROLE_CODE_NOT_FOUNT.getCode();
            case "操作日志已存在":
                return ResultCode.OPERATION_LOG_EXISTS.getCode();
            case "操作日志不存在":
                return ResultCode.OPERATION_LOG_NOT_FOUNT.getCode();
            case "访问日志已存在":
                return ResultCode.ACCESS_LOG_EXISTS.getCode();
            case "访问日志不存在":
                return ResultCode.ACCESS_LOG_NOT_FOUNT.getCode();
            case "角色已存在":
                return ResultCode.ROLE_EXISTS.getCode();
            case "角色不存在":
                return ResultCode.ROLE_NOT_FOUNT.getCode();
            case "权限已存在":
                return ResultCode.PERMISSION_EXISTS.getCode();
            case "权限不存在":
                return ResultCode.PERMISSION_NOT_FOUNT.getCode();
            case "用户不存在":
                return ResultCode.USER_NOT_FOUND.getCode();
            case "用户名或密码错误":
                return ResultCode.USER_PASSWORD_ERROR.getCode();
            case "用户已被禁用":
                return ResultCode.USER_DISABLED.getCode();
            case "用户已存在":
                return ResultCode.USER_ALREADY_EXISTS.getCode();
            case "新旧密码不能相同":
                return ResultCode.USER_PASSWORD_NOT_DIFFERENT.getCode();
            case "Token 无效":
                return ResultCode.TOKEN_INVALID.getCode();
            case "Token 已过期":
                return ResultCode.TOKEN_EXPIRED.getCode();
            case "没有操作权限":
                return ResultCode.NO_PERMISSION.getCode();
            default:
                return ResultCode.FAIL.getCode();
        }
    }
}
