package com.ccs.ipc.common.aspect;

import com.ccs.ipc.common.annotation.Log;
import com.ccs.ipc.common.annotation.UsernameField;
import com.ccs.ipc.common.util.IpUtil;
import com.ccs.ipc.common.util.SensitiveUtil;
import com.ccs.ipc.common.util.UserContext;
import com.ccs.ipc.entity.SysOperationLog;
import com.ccs.ipc.service.ISysOperationLogService;
import com.ccs.ipc.service.ISysUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * 操作日志切面
 *
 * @author WZH
 * @since 2026-01-19
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

    @Autowired
    private ISysOperationLogService sysOperationLogService;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 配置切点：拦截所有标注了@Log注解的方法
     */
    @Pointcut("@annotation(com.ccs.ipc.common.annotation.Log)")
    public void logPointcut() {
    }

    /**
     * 环绕通知：记录操作日志
     *
     * @param joinPoint 连接点
     * @return 方法执行结果
     * @throws Throwable 异常
     */
    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }

        long startTime = System.currentTimeMillis();
        SysOperationLog operationLog = new SysOperationLog();
        Object result = null;
        Exception exception = null;

        try {
            // 获取请求信息
            HttpServletRequest request = attributes.getRequest();

            // 获取注解信息
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Log logAnnotation = method.getAnnotation(Log.class);

            // 设置操作类型、模块、描述
            operationLog.setOperationType(logAnnotation.operationType().getValue());
            operationLog.setOperationModule(logAnnotation.operationModule().getValue());
            operationLog.setOperationDesc(logAnnotation.operationDesc());

            // 设置请求信息
            operationLog.setRequestMethod(request.getMethod());
            operationLog.setRequestUrl(request.getRequestURI());
            operationLog.setIpAddress(IpUtil.getIpAddress(request));
            operationLog.setUserAgent(request.getHeader("User-Agent"));

            // 获取用户信息
            // 优先从JWT Token中获取（有token的接口）
            Long userId = UserContext.getUserId(request);
            String username = UserContext.getUsername(request);
            
            // 如果从Token中获取不到（如登录接口），则从请求参数中提取
            if (username == null) {
                username = extractUsernameFromArgs(joinPoint.getArgs());
            }
            
            operationLog.setUserId(userId);
            operationLog.setUsername(username != null ? username : "匿名用户");

            if (logAnnotation.saveRequestData()) {
                Object[] args = joinPoint.getArgs();
                if (args != null && args.length > 0) {
                    try {
                        // 过滤掉HttpServletRequest等特殊参数
                        StringBuilder params = new StringBuilder();
                        for (Object arg : args) {
                            if (arg != null && !(arg instanceof HttpServletRequest)) {
                                if (params.length() > 0) {
                                    params.append(", ");
                                }
                                // 使用脱敏工具序列化
                                String json = SensitiveUtil.toJsonWithDesensitize(arg, objectMapper);
                                // 限制参数长度，避免过长
                                if (json.length() > 2000) {
                                    json = json.substring(0, 2000) + "...";
                                }
                                params.append(json);
                            }
                        }
                        operationLog.setRequestParams(params.toString());
                    } catch (Exception e) {
                        log.warn("序列化请求参数失败: {}", e.getMessage());
                    }
                }
            }

            // 执行目标方法
            result = joinPoint.proceed();

            // 保存响应数据（带脱敏）
            if (logAnnotation.saveResponseData()) {
                if (result != null) {
                    try {
                        // 使用脱敏工具序列化
                        String responseJson = SensitiveUtil.toJsonWithDesensitize(result, objectMapper);
                        // 限制响应数据长度
                        if (responseJson.length() > 2000) {
                            responseJson = responseJson.substring(0, 2000) + "...";
                        }
                        operationLog.setResponseData(responseJson);
                    } catch (Exception e) {
                        log.warn("序列化响应数据失败: {}", e.getMessage());
                        operationLog.setResponseData("序列化响应数据失败: " + e.getMessage());
                    }
                } else {
                    // 响应为null时，记录为null标记
                    operationLog.setResponseData("null");
                }
            }

            // 设置操作状态为成功
            operationLog.setStatus((byte) 1);

        } catch (Exception e) {
            exception = e;
            operationLog.setStatus((byte) 0);
            operationLog.setErrorMsg(getExceptionMessage(e));
            throw e;
        } finally {
            // 计算执行时间
            long executionTime = System.currentTimeMillis() - startTime;
            operationLog.setExecutionTime(executionTime);

            // 如果userId为空但username不为空，尝试根据username查询userId（如登录接口）
            if (operationLog.getUserId() == null && operationLog.getUsername() != null 
                    && !"匿名用户".equals(operationLog.getUsername())) {
                try {
                    Long userId = sysUserService.getUserIdByUsername(operationLog.getUsername());
                    if (userId != null) {
                        operationLog.setUserId(userId);
                    }
                } catch (Exception e) {
                    log.debug("根据用户名查询用户ID失败: {}", e.getMessage());
                }
            }

            // 真正的异步保存日志（使用@Async，避免影响主流程性能）
            sysOperationLogService.saveAsync(operationLog);
        }

        return result;
    }

    /**
     * 从请求参数中提取用户名（用于登录等没有token的接口）
     * 通过@UsernameField注解识别用户名字段
     *
     * @param args 方法参数
     * @return 用户名
     */
    private String extractUsernameFromArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return null;
        }

        for (Object arg : args) {
            if (arg == null || arg instanceof HttpServletRequest) {
                continue;
            }

            try {
                // 通过反射查找标注了@UsernameField注解的字段
                java.lang.reflect.Field[] fields = arg.getClass().getDeclaredFields();
                for (java.lang.reflect.Field field : fields) {
                    // 检查字段是否有@UsernameField注解
                    if (field.isAnnotationPresent(UsernameField.class)) {
                        field.setAccessible(true);
                        Object value = field.get(arg);
                        if (value != null) {
                            return value.toString();
                        }
                    }
                }
            } catch (Exception e) {
                // 忽略反射异常
                log.debug("提取用户名失败: {}", e.getMessage());
            }
        }

        return null;
    }

    /**
     * 获取异常信息
     *
     * @param e 异常
     * @return 异常信息
     */
    private String getExceptionMessage(Exception e) {
        String message = e.getMessage();
        if (!StringUtils.hasText(message)) {
            message = e.getClass().getName();
        }
        // 限制错误信息长度
        if (message.length() > 1000) {
            message = message.substring(0, 1000) + "...";
        }
        return message;
    }
}

