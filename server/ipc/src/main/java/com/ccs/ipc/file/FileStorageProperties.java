package com.ccs.ipc.file;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 本地文件存储配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "ipc.file")
public class FileStorageProperties {

    /**
     * 上传根目录（磁盘路径）。默认相对路径 uploads（相对启动目录）。
     */
    private String uploadDir = "uploads";

    /**
     * 对外访问的URL前缀（静态资源映射路径）
     */
    private String publicPath = "/uploads";

    /**
     * 单文件最大大小（字节），默认 10MB
     */
    private Long maxSizeBytes = 10 * 1024 * 1024L;
}

