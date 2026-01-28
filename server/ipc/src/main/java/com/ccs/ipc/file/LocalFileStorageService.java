package com.ccs.ipc.file;

import com.ccs.ipc.dto.common.FileUploadResponse;
import com.ccs.ipc.entity.SysFile;
import com.ccs.ipc.service.ISysFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 本地磁盘文件存储 + sys_file 记录
 */
@Service
@RequiredArgsConstructor
public class LocalFileStorageService {

    private static final DateTimeFormatter DAY = DateTimeFormatter.BASIC_ISO_DATE; // yyyyMMdd

    private final FileStorageProperties properties;
    private final ISysFileService sysFileService;

    public FileUploadResponse upload(MultipartFile file, Long uploadUserId, String businessType, Long businessId) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("请选择要上传的文件");
        }
        Long max = properties.getMaxSizeBytes();
        if (max != null && max > 0 && file.getSize() > max) {
            throw new RuntimeException("文件过大，最大允许 " + (max / 1024 / 1024) + "MB");
        }

        String originalName = file.getOriginalFilename();
        String mimeType = file.getContentType();
        String ext = getExtension(originalName, mimeType);

        String safeBusiness = StringUtils.hasText(businessType) ? businessType.trim() : "common";
        String date = LocalDate.now().format(DAY);
        String key = UUID.randomUUID().toString().replace("-", "");

        String relativePath = safeBusiness + "/" + date + "/" + key + ext; // 用于URL映射
        Path root = Paths.get(properties.getUploadDir()).toAbsolutePath().normalize();
        Path target = root.resolve(relativePath).normalize();

        try {
            Files.createDirectories(target.getParent());
            file.transferTo(target);
        } catch (IOException e) {
            throw new RuntimeException("保存文件失败：" + e.getMessage(), e);
        }

        String publicPath = normalizePublicPath(properties.getPublicPath());
        String fileUrl = publicPath + "/" + relativePath.replace("\\", "/");

        SysFile record = new SysFile();
        record.setFileName(StringUtils.hasText(originalName) ? originalName : ("file" + ext));
        record.setFilePath(target.toString());
        record.setFileUrl(fileUrl);
        record.setMimeType(mimeType);
        record.setFileSize(file.getSize());
        record.setUploadUserId(uploadUserId);
        record.setBusinessType(safeBusiness);
        record.setBusinessId(businessId);
        record.setFileType(classifyFileType(mimeType, ext));
        record.setIsDeleted((byte) 0);
        sysFileService.save(record);

        FileUploadResponse resp = new FileUploadResponse();
        resp.setFileId(record.getId());
        resp.setFileName(record.getFileName());
        resp.setFileUrl(record.getFileUrl());
        resp.setMimeType(record.getMimeType());
        resp.setFileSize(record.getFileSize());
        resp.setFileType(record.getFileType());
        return resp;
    }

    private String normalizePublicPath(String p) {
        String v = StringUtils.hasText(p) ? p.trim() : "/uploads";
        if (!v.startsWith("/")) v = "/" + v;
        if (v.endsWith("/")) v = v.substring(0, v.length() - 1);
        return v;
    }

    private String classifyFileType(String mimeType, String ext) {
        String mt = mimeType == null ? "" : mimeType.toLowerCase();
        if (mt.startsWith("image/")) return "image";
        if (mt.startsWith("video/")) return "video";
        if (mt.startsWith("audio/")) return "audio";
        if (mt.contains("pdf") || mt.contains("word") || mt.contains("excel") || mt.contains("text")) return "document";
        if (ext != null && (ext.equalsIgnoreCase(".png") || ext.equalsIgnoreCase(".jpg") || ext.equalsIgnoreCase(".jpeg") || ext.equalsIgnoreCase(".gif") || ext.equalsIgnoreCase(".webp"))) {
            return "image";
        }
        return "other";
    }

    private String getExtension(String originalName, String mimeType) {
        String ext = "";
        if (StringUtils.hasText(originalName) && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf('.'));
            // 粗略防注入：只允许常见长度
            if (ext.length() > 10) ext = "";
        }
        if (StringUtils.hasText(ext)) {
            return ext.startsWith(".") ? ext : ("." + ext);
        }
        String mt = mimeType == null ? "" : mimeType.toLowerCase();
        if (mt.equals("image/jpeg")) return ".jpg";
        if (mt.equals("image/png")) return ".png";
        if (mt.equals("image/gif")) return ".gif";
        if (mt.equals("image/webp")) return ".webp";
        if (mt.equals("application/pdf")) return ".pdf";
        return "";
    }
}

