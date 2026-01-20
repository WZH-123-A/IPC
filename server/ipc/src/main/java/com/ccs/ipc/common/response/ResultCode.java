package com.ccs.ipc.common.response;

public enum ResultCode {

    /* ======= 通用 ======= */
    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),

    /* ======= 客户端错误 ======= */
    BAD_REQUEST(400, "请求参数错误"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    UNSUPPORTED_MEDIA_TYPE(415, "不支持的媒体类型"),

    /* ======= 认证 & 授权 ======= */
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),

    /* ======= 通用业务异常 1000-1999 ======= */
    BUSINESS_ERROR(1000, "业务异常"),
    PARAM_VALIDATE_ERROR(1001, "参数校验失败"),
    DATA_NOT_FOUND(1002, "数据不存在"),
    DATA_ALREADY_EXISTS(1003, "数据已存在"),

    /* ======= 用户模块 2000-2999 ======= */
    USER_NOT_FOUND(2000, "用户不存在"),
    USER_PASSWORD_ERROR(2001, "用户名或密码错误"),
    USER_DISABLED(2002, "用户已被禁用"),
    USER_ALREADY_EXISTS(2003, "用户已存在"),

    /* ======= 权限模块 3000-3999 ======= */
    TOKEN_INVALID(3001, "Token 无效"),
    TOKEN_EXPIRED(3002, "Token 已过期"),
    NO_PERMISSION(3003, "没有操作权限");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
