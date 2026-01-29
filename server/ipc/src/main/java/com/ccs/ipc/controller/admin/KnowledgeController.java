package com.ccs.ipc.controller.admin;

import com.ccs.ipc.common.annotation.Log;
import com.ccs.ipc.common.annotation.RequirePermission;
import com.ccs.ipc.common.enums.OperationModule;
import com.ccs.ipc.common.enums.OperationType;
import com.ccs.ipc.common.response.Response;
import com.ccs.ipc.common.util.UserContext;
import com.ccs.ipc.dto.common.FileUploadResponse;
import com.ccs.ipc.dto.admindto.*;
import com.ccs.ipc.file.LocalFileStorageService;
import com.ccs.ipc.service.IKnowledgeCategoryService;
import com.ccs.ipc.service.IKnowledgeContentService;
import com.ccs.ipc.service.IKnowledgeTagService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 管理员 - 知识库管理（分类、内容、标签、图片上传）
 *
 * @author WZH
 * @since 2026-01-29
 */
@RestController
@RequestMapping("/api/admin/knowledge")
public class KnowledgeController {

    @Autowired
    private IKnowledgeCategoryService knowledgeCategoryService;

    @Autowired
    private IKnowledgeContentService knowledgeContentService;

    @Autowired
    private IKnowledgeTagService knowledgeTagService;

    @Autowired
    private LocalFileStorageService localFileStorageService;

    // ==================== 图片上传 ====================

    /**
     * 知识库图片上传（封面或正文插图），返回可访问的 fileUrl
     */
    @PostMapping("/upload")
    @RequirePermission("admin:api:knowledge:upload")
    @Log(operationType = OperationType.ADD, operationModule = OperationModule.FILE, operationDesc = "知识库图片上传")
    public Response<FileUploadResponse> uploadImage(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        Long userId = UserContext.getUserId(request);
        if (userId == null) {
            return Response.fail("未登录");
        }
        try {
            FileUploadResponse resp = localFileStorageService.upload(file, userId, "knowledge", null);
            return Response.success(resp);
        } catch (Exception e) {
            return Response.fail(e.getMessage());
        }
    }

    // ==================== 分类 ====================

    @GetMapping("/categories/list")
    @RequirePermission("admin:api:knowledge-category:list")
    public Response<KnowledgeCategoryListResponse> getCategoryList(KnowledgeCategoryListRequest request) {
        KnowledgeCategoryListResponse response = knowledgeCategoryService.getAdminCategoryList(request);
        return Response.success(response);
    }

    @GetMapping("/categories/{id}")
    @RequirePermission("admin:api:knowledge-category:detail")
    public Response<KnowledgeCategoryResponse> getCategoryById(@PathVariable Long id) {
        KnowledgeCategoryResponse response = knowledgeCategoryService.getAdminCategoryById(id);
        if (response == null) return Response.fail("分类不存在");
        return Response.success(response);
    }

    @PostMapping("/categories")
    @RequirePermission("admin:api:knowledge-category:create")
    @Log(operationType = OperationType.ADD, operationModule = OperationModule.OTHER, operationDesc = "新增知识库分类")
    public Response<KnowledgeCategoryResponse> createCategory(@Valid @RequestBody KnowledgeCategoryCreateRequest request) {
        KnowledgeCategoryResponse response = knowledgeCategoryService.createAdminCategory(request);
        return Response.success(response);
    }

    @PutMapping("/categories/{id}")
    @RequirePermission("admin:api:knowledge-category:update")
    @Log(operationType = OperationType.UPDATE, operationModule = OperationModule.OTHER, operationDesc = "更新知识库分类")
    public Response<Void> updateCategory(@PathVariable Long id, @Valid @RequestBody KnowledgeCategoryUpdateRequest request) {
        knowledgeCategoryService.updateAdminCategory(id, request);
        return Response.success();
    }

    @DeleteMapping("/categories/{id}")
    @RequirePermission("admin:api:knowledge-category:delete")
    @Log(operationType = OperationType.DELETE, operationModule = OperationModule.OTHER, operationDesc = "删除知识库分类")
    public Response<Void> deleteCategory(@PathVariable Long id) {
        knowledgeCategoryService.deleteAdminCategory(id);
        return Response.success();
    }

    // ==================== 内容 ====================

    @GetMapping("/contents/list")
    @RequirePermission("admin:api:knowledge-content:list")
    public Response<AdminKnowledgeContentListResponse> getContentList(AdminKnowledgeContentListRequest request) {
        AdminKnowledgeContentListResponse response = knowledgeContentService.getAdminContentList(request);
        return Response.success(response);
    }

    @GetMapping("/contents/{id}")
    @RequirePermission("admin:api:knowledge-content:detail")
    public Response<KnowledgeContentResponse> getContentById(@PathVariable Long id) {
        KnowledgeContentResponse response = knowledgeContentService.getAdminContentById(id);
        if (response == null) return Response.fail("内容不存在");
        return Response.success(response);
    }

    @PostMapping("/contents")
    @RequirePermission("admin:api:knowledge-content:create")
    @Log(operationType = OperationType.ADD, operationModule = OperationModule.OTHER, operationDesc = "新增知识库内容")
    public Response<KnowledgeContentResponse> createContent(
            @Valid @RequestBody KnowledgeContentCreateRequest request,
            HttpServletRequest httpRequest) {
        Long userId = UserContext.getUserId(httpRequest);
        KnowledgeContentResponse response = knowledgeContentService.createAdminContent(request, userId);
        return Response.success(response);
    }

    @PutMapping("/contents/{id}")
    @RequirePermission("admin:api:knowledge-content:update")
    @Log(operationType = OperationType.UPDATE, operationModule = OperationModule.OTHER, operationDesc = "更新知识库内容")
    public Response<Void> updateContent(@PathVariable Long id, @RequestBody KnowledgeContentUpdateRequest request) {
        knowledgeContentService.updateAdminContent(id, request);
        return Response.success();
    }

    @DeleteMapping("/contents/{id}")
    @RequirePermission("admin:api:knowledge-content:delete")
    @Log(operationType = OperationType.DELETE, operationModule = OperationModule.OTHER, operationDesc = "删除知识库内容")
    public Response<Void> deleteContent(@PathVariable Long id) {
        knowledgeContentService.deleteAdminContent(id);
        return Response.success();
    }

    // ==================== 标签 ====================

    @GetMapping("/tags/list")
    @RequirePermission("admin:api:knowledge-tag:list")
    public Response<KnowledgeTagListResponse> getTagList(KnowledgeTagListRequest request) {
        KnowledgeTagListResponse response = knowledgeTagService.getAdminTagList(request);
        return Response.success(response);
    }

    @GetMapping("/tags/{id}")
    @RequirePermission("admin:api:knowledge-tag:detail")
    public Response<KnowledgeTagResponse> getTagById(@PathVariable Long id) {
        KnowledgeTagResponse response = knowledgeTagService.getAdminTagById(id);
        if (response == null) return Response.fail("标签不存在");
        return Response.success(response);
    }

    @PostMapping("/tags")
    @RequirePermission("admin:api:knowledge-tag:create")
    @Log(operationType = OperationType.ADD, operationModule = OperationModule.OTHER, operationDesc = "新增知识库标签")
    public Response<KnowledgeTagResponse> createTag(@Valid @RequestBody KnowledgeTagCreateRequest request) {
        KnowledgeTagResponse response = knowledgeTagService.createAdminTag(request);
        return Response.success(response);
    }

    @PutMapping("/tags/{id}")
    @RequirePermission("admin:api:knowledge-tag:update")
    @Log(operationType = OperationType.UPDATE, operationModule = OperationModule.OTHER, operationDesc = "更新知识库标签")
    public Response<Void> updateTag(@PathVariable Long id, @Valid @RequestBody KnowledgeTagUpdateRequest request) {
        knowledgeTagService.updateAdminTag(id, request);
        return Response.success();
    }

    @DeleteMapping("/tags/{id}")
    @RequirePermission("admin:api:knowledge-tag:delete")
    @Log(operationType = OperationType.DELETE, operationModule = OperationModule.OTHER, operationDesc = "删除知识库标签")
    public Response<Void> deleteTag(@PathVariable Long id) {
        knowledgeTagService.deleteAdminTag(id);
        return Response.success();
    }
}
