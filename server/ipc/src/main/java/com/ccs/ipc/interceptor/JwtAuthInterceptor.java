package com.ccs.ipc.interceptor;

import com.ccs.ipc.common.exception.ApiException;
import com.ccs.ipc.common.response.ResultCode;
import com.ccs.ipc.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT认证拦截器
 *
 * @author WZH
 * @since 2026-01-19
 */
@Slf4j
@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS请求直接放行
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // 获取Token
        String token = getTokenFromRequest(request);

        if (!StringUtils.hasText(token)) {
            throw new ApiException(ResultCode.UNAUTHORIZED.getMessage());
        }

        // 验证Token
        if (!jwtUtil.validateToken(token)) {
            throw new ApiException(ResultCode.TOKEN_INVALID.getMessage());
        }

        // 将用户信息存储到request中，方便后续使用
        Long userId = jwtUtil.getUserIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);
        request.setAttribute("userId", userId);
        request.setAttribute("username", username);

        return true;
    }

    /**
     * 从请求中获取Token
     *
     * @param request HTTP请求
     * @return Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(TOKEN_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}

