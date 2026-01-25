package com.ccs.ipc.interceptor;

import com.ccs.ipc.common.annotation.RequirePermission;
import com.ccs.ipc.common.exception.ApiException;
import com.ccs.ipc.common.response.ResultCode;
import com.ccs.ipc.common.util.UserContext;
import com.ccs.ipc.service.ISysUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

/**
 * 权限拦截器
 * 检查用户是否有权限访问接口
 *
 * @author WZH
 * @since 2026-01-19
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    private ISysUserService sysUserService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是方法处理器，直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // 检查方法上的权限注解
        RequirePermission methodPermission = handlerMethod.getMethodAnnotation(RequirePermission.class);
        if (methodPermission != null) {
            checkPermission(request, methodPermission.value());
            return true;
        }

        // 检查类上的权限注解
        RequirePermission classPermission = handlerMethod.getBeanType().getAnnotation(RequirePermission.class);
        if (classPermission != null) {
            checkPermission(request, classPermission.value());
            return true;
        }

        // 没有权限注解，直接放行
        return true;
    }

    /**
     * 检查用户权限
     *
     * @param request         HTTP请求
     * @param requiredPermission 需要的权限编码
     */
    private void checkPermission(HttpServletRequest request, String requiredPermission) {
        // 从request中获取用户ID（由JwtAuthInterceptor设置）
        Long userId = UserContext.getUserId(request);
        if (userId == null) {
            throw new ApiException(ResultCode.UNAUTHORIZED.getMessage());
        }

        // 获取用户的所有权限（API权限，类型为3）
        List<String> userPermissions = sysUserService.getUserApiPermissions(userId);

        // 检查是否有权限
        if (!userPermissions.contains(requiredPermission)) {
            throw new ApiException(ResultCode.NO_PERMISSION.getMessage());
        }
    }
}

