package com.ccs.ipc.common.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户上下文工具类
 * 用于获取当前登录用户信息
 *
 * @author WZH
 * @since 2026-01-19
 */
public class UserContext {

    private static final String USER_ID_ATTRIBUTE = "userId";
    private static final String USERNAME_ATTRIBUTE = "username";

    /**
     * 从请求中获取当前登录用户ID
     *
     * @param request HTTP请求
     * @return 用户ID
     */
    public static Long getUserId(HttpServletRequest request) {
        Object userId = request.getAttribute(USER_ID_ATTRIBUTE);
        if (userId instanceof Long) {
            return (Long) userId;
        } else if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        }
        return null;
    }

    /**
     * 从请求中获取当前登录用户名
     *
     * @param request HTTP请求
     * @return 用户名
     */
    public static String getUsername(HttpServletRequest request) {
        Object username = request.getAttribute(USERNAME_ATTRIBUTE);
        return username != null ? username.toString() : null;
    }
}

