package com.ccs.ipc.config;

import com.ccs.ipc.interceptor.JwtAuthInterceptor;
import com.ccs.ipc.interceptor.PermissionInterceptor;
import com.ccs.ipc.file.FileStorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Web配置类
 *
 * @author WZH
 * @since 2026-01-19
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtAuthInterceptor jwtAuthInterceptor;

    @Autowired
    private PermissionInterceptor permissionInterceptor;

    @Autowired
    private FileStorageProperties fileStorageProperties;

    /**
     * 配置拦截器
     *
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // JWT认证拦截器（先执行）
        registry.addInterceptor(jwtAuthInterceptor)
                .addPathPatterns("/api/**") // 拦截所有/api/**路径
                .excludePathPatterns(
                        "/api/auth/login",  // 登录接口不需要认证
                        "/api/ws",          // WebSocket 端点（SockJS）
                        "/api/ws/**",       // WebSocket 端点（SockJS 子路径）
                        "/api/ws-native",   // WebSocket 端点（原生）
                        "/api/ws-native/**", // WebSocket 端点（原生子路径）
                        "/error",           // 错误页面不需要认证
                        "/actuator/**"      // 监控端点不需要认证（如果有的话）
                )
                .order(1);

        // 权限拦截器（后执行，在JWT认证之后）
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/auth/login",  // 登录接口不需要权限检查
                        "/api/ws",          // WebSocket 端点（SockJS）
                        "/api/ws/**",       // WebSocket 端点（SockJS 子路径）
                        "/api/ws-native",   // WebSocket 端点（原生）
                        "/api/ws-native/**", // WebSocket 端点（原生子路径）
                        "/error",
                        "/actuator/**"
                )
                .order(2);
    }

    /**
     * 配置跨域
     *
     * @param registry 跨域注册表
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * 静态资源映射：将 /uploads/** 映射到本地磁盘上传目录
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String publicPath = fileStorageProperties.getPublicPath();
        if (publicPath == null || publicPath.isBlank()) {
            publicPath = "/uploads";
        }
        if (!publicPath.startsWith("/")) {
            publicPath = "/" + publicPath;
        }
        if (publicPath.endsWith("/")) {
            publicPath = publicPath.substring(0, publicPath.length() - 1);
        }

        Path root = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
        String location = "file:" + root.toString() + "/";

        registry.addResourceHandler(publicPath + "/**")
                .addResourceLocations(location);
    }
}

