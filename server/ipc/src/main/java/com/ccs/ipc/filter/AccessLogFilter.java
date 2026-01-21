package com.ccs.ipc.filter;

import com.ccs.ipc.common.util.IpUtil;
import com.ccs.ipc.common.util.UserContext;
import com.ccs.ipc.entity.SysAccessLog;
import com.ccs.ipc.service.ISysAccessLogService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * 访问日志过滤器
 * 记录所有API请求的访问日志，包括HTTP方法不匹配等错误情况
 * 
 * @author WZH
 * @since 2026-01-21
 */
@Slf4j
@Component
@Order(1) // 确保在其他Filter之前执行
public class AccessLogFilter implements Filter {

    @Autowired
    private ISysAccessLogService sysAccessLogService;

    private static final ThreadLocal<Long> START_TIME = new ThreadLocal<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 只处理 /api/** 路径的请求
        String requestURI = httpRequest.getRequestURI();
        if (!requestURI.startsWith("/api/")) {
            chain.doFilter(request, response);
            return;
        }

        // 记录开始时间
        long startTime = System.currentTimeMillis();
        START_TIME.set(startTime);

        try {
            // 执行后续的Filter和DispatcherServlet
            chain.doFilter(request, response);
        } finally {
            // 无论成功还是异常，都记录访问日志
            try {
                long executionTime = System.currentTimeMillis() - startTime;
                recordAccessLog(httpRequest, httpResponse, executionTime);
            } catch (Exception e) {
                log.error("记录访问日志失败: {}", e.getMessage(), e);
            } finally {
                // 清理ThreadLocal
                START_TIME.remove();
            }
        }
    }

    /**
     * 记录访问日志
     */
    private void recordAccessLog(HttpServletRequest request, HttpServletResponse response, long executionTime) {
        SysAccessLog accessLog = new SysAccessLog();

        // 设置请求信息
        accessLog.setRequestMethod(request.getMethod());
        accessLog.setRequestUrl(request.getRequestURI());
        accessLog.setIpAddress(IpUtil.getIpAddress(request));
        accessLog.setUserAgent(request.getHeader("User-Agent"));

        // 设置用户信息
        Long userId = UserContext.getUserId(request);
        String username = UserContext.getUsername(request);
        accessLog.setUserId(userId);
        accessLog.setUsername(username != null ? username : "匿名用户");

        // 设置响应状态码
        accessLog.setResponseStatus(response.getStatus());

        // 设置执行时间
        accessLog.setExecutionTime(executionTime);

        // 记录请求参数（只记录GET请求的查询参数，POST等请求参数可能很大，不记录）
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            String queryString = request.getQueryString();
            if (StringUtils.hasText(queryString)) {
                // 限制参数长度
                if (queryString.length() > 500) {
                    queryString = queryString.substring(0, 500) + "...";
                }
                accessLog.setRequestParams(queryString);
            }
        }

        // 异步保存访问日志
        sysAccessLogService.saveAsync(accessLog);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}

