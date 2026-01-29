package com.ccs.ipc.controller.patient;

import com.ccs.ipc.common.annotation.Log;
import com.ccs.ipc.common.annotation.RequirePermission;
import com.ccs.ipc.common.enums.log.PatientModule;
import com.ccs.ipc.common.enums.log.PatientOperation;
import com.ccs.ipc.common.response.Response;
import com.ccs.ipc.common.util.UserContext;
import com.ccs.ipc.dto.common.FileUploadResponse;
import com.ccs.ipc.dto.patientdto.*;
import com.ccs.ipc.entity.ConsultationSession;
import com.ccs.ipc.file.LocalFileStorageService;
import com.ccs.ipc.service.*;
import com.ccs.ipc.service.IConsultationEvaluationService;
import com.ccs.ipc.service.IKnowledgeCategoryService;
import com.ccs.ipc.service.IKnowledgeContentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 患者控制器
 * 提供患者相关的问诊和诊断功能
 *
 * @author WZH
 * @since 2026-01-27
 */
@RestController
@RequestMapping("/api/patient")
public class PatientController {

    @Autowired
    private IConsultationSessionService consultationSessionService;

    @Autowired
    private IConsultationMessageService consultationMessageService;

    @Autowired
    private ISkinDiagnosisRecordService skinDiagnosisRecordService;

    @Autowired
    private IDiagnosisResultService diagnosisResultService;

    @Autowired
    private IDiseaseTypeService diseaseTypeService;

    @Autowired
    private IDoctorInfoService doctorInfoService;

    @Autowired
    private LocalFileStorageService localFileStorageService;

    @Autowired
    private IConsultationEvaluationService consultationEvaluationService;

    @Autowired
    private IKnowledgeCategoryService knowledgeCategoryService;

    @Autowired
    private IKnowledgeContentService knowledgeContentService;

    // ==================== 问诊相关接口 ====================

    /**
     * 获取问诊会话列表
     */
    @GetMapping("/consultation/sessions")
    @RequirePermission("patient:api:consultation-session:list")
    @Log(operationType = PatientOperation.C.QUERY_SESSIONS, operationModule = PatientModule.C.PATIENT, operationDesc = "获取问诊会话列表")
    public Response<ConsultationSessionListResponse> getConsultationSessions(
            ConsultationSessionListRequest request,
            HttpServletRequest httpRequest) {
        Long userId = UserContext.getUserId(httpRequest);
        try {
            ConsultationSessionListResponse response = consultationSessionService.getPatientSessions(userId, request);
            return Response.success(response);
        } catch (Exception e) {
            return Response.fail(e.getMessage());
        }
    }

    /**
     * 获取问诊会话详情
     */
    @GetMapping("/consultation/sessions/{sessionId}")
    @RequirePermission("patient:api:consultation-session:detail")
    @Log(operationType = PatientOperation.C.QUERY_SESSION_DETAIL, operationModule = PatientModule.C.PATIENT, operationDesc = "获取问诊会话详情")
    public Response<ConsultationSessionResponse> getConsultationSession(
            @PathVariable Long sessionId,
            HttpServletRequest httpRequest) {
        Long userId = UserContext.getUserId(httpRequest);
        try {
            ConsultationSessionResponse response = consultationSessionService.getSessionDetail(sessionId, userId);
            return Response.success(response);
        } catch (Exception e) {
            return Response.fail(e.getMessage());
        }
    }

    /**
     * 创建问诊会话
     */
    @PostMapping("/consultation/sessions")
    @RequirePermission("patient:api:consultation-session:create")
    @Log(operationType = PatientOperation.C.CREATE_SESSION, operationModule = PatientModule.C.PATIENT, operationDesc = "创建问诊会话")
    public Response<ConsultationSessionResponse> createConsultationSession(
            @Valid @RequestBody CreateConsultationSessionRequest request,
            HttpServletRequest httpRequest) {
        Long userId = UserContext.getUserId(httpRequest);
        try {
            ConsultationSessionResponse response = consultationSessionService.createSession(userId, request);
            return Response.success(response);
        } catch (Exception e) {
            return Response.fail(e.getMessage());
        }
    }

    /**
     * 结束问诊会话
     */
    @PostMapping("/consultation/sessions/{sessionId}/end")
    @RequirePermission("patient:api:consultation-session:update")
    @Log(operationType = PatientOperation.C.END_SESSION, operationModule = PatientModule.C.PATIENT, operationDesc = "结束问诊会话")
    public Response<Void> endConsultationSession(
            @PathVariable Long sessionId,
            HttpServletRequest httpRequest) {
        Long userId = UserContext.getUserId(httpRequest);
        try {
            consultationSessionService.endSession(sessionId, userId);
            return Response.success();
        } catch (Exception e) {
            return Response.fail(e.getMessage());
        }
    }

    /**
     * 获取问诊消息列表
     */
    @GetMapping("/consultation/sessions/{sessionId}/messages")
    @RequirePermission("patient:api:consultation-message:list")
    @Log(operationType = PatientOperation.C.QUERY_MESSAGES, operationModule = PatientModule.C.PATIENT, operationDesc = "获取问诊消息列表")
    public Response<ConsultationMessageListResponse> getConsultationMessages(
            @PathVariable Long sessionId,
            ConsultationMessageListRequest request,
            HttpServletRequest httpRequest) {
        Long userId = UserContext.getUserId(httpRequest);
        try {
            ConsultationMessageListResponse response = consultationMessageService.getSessionMessages(sessionId, userId, request);
            return Response.success(response);
        } catch (Exception e) {
            return Response.fail(e.getMessage());
        }
    }

    /**
     * 发送消息
     */
    @PostMapping("/consultation/messages")
    @RequirePermission("patient:api:consultation-message:create")
    @Log(operationType = PatientOperation.C.SEND_MESSAGE, operationModule = PatientModule.C.PATIENT, operationDesc = "发送问诊消息")
    public Response<ConsultationMessageResponse> sendMessage(
            @Valid @RequestBody SendMessageRequest request,
            HttpServletRequest httpRequest) {
        Long userId = UserContext.getUserId(httpRequest);
        try {
            ConsultationMessageResponse response = consultationMessageService.sendMessage(userId, request);
            return Response.success(response);
        } catch (Exception e) {
            return Response.fail(e.getMessage());
        }
    }

    /**
     * 标记消息为已读
     */
    @PostMapping("/consultation/messages/{messageId}/read")
    @RequirePermission("patient:api:consultation-message:update")
    @Log(operationType = PatientOperation.C.MARK_MESSAGE_READ, operationModule = PatientModule.C.PATIENT, operationDesc = "标记消息为已读")
    public Response<Void> markMessageAsRead(
            @PathVariable Long messageId,
            HttpServletRequest httpRequest) {
        try {
            consultationMessageService.markAsRead(messageId);
            return Response.success();
        } catch (Exception e) {
            return Response.fail(e.getMessage());
        }
    }

    /**
     * 批量标记会话的所有未读消息为已读
     */
    @PostMapping("/consultation/sessions/{sessionId}/messages/mark-all-read")
    @RequirePermission("patient:api:consultation-message:update")
    @Log(operationType = PatientOperation.C.MARK_ALL_READ, operationModule = PatientModule.C.PATIENT, operationDesc = "批量标记消息为已读")
    public Response<Void> markAllMessagesAsRead(
            @PathVariable Long sessionId,
            HttpServletRequest httpRequest) {
        Long userId = UserContext.getUserId(httpRequest);
        try {
            consultationMessageService.markAllAsRead(sessionId, userId);
            return Response.success();
        } catch (Exception e) {
            return Response.fail(e.getMessage());
        }
    }

    /**
     * 获取当前会话的评价（已评价则返回评价内容，未评价则无数据）
     */
    @GetMapping("/consultation/sessions/{sessionId}/evaluation")
    @RequirePermission("patient:api:consultation-evaluation:detail")
    @Log(operationType = PatientOperation.C.QUERY_EVALUATION, operationModule = PatientModule.C.PATIENT, operationDesc = "获取会话评价")
    public Response<EvaluationResponse> getSessionEvaluation(
            @PathVariable Long sessionId,
            HttpServletRequest httpRequest) {
        Long userId = UserContext.getUserId(httpRequest);
        try {
            ConsultationSession session = consultationSessionService.getById(sessionId);
            if (session == null || session.getIsDeleted() == 1 || !session.getPatientId().equals(userId)) {
                return Response.fail("无权查看");
            }
            EvaluationResponse response = consultationEvaluationService.getBySessionId(sessionId);
            return Response.success(response);
        } catch (Exception e) {
            return Response.fail(e.getMessage());
        }
    }

    /**
     * 患者提交问诊评价（仅已结束的会话、每个会话仅可评价一次）
     */
    @PostMapping("/consultation/sessions/{sessionId}/evaluation")
    @RequirePermission("patient:api:consultation-evaluation:create")
    @Log(operationType = PatientOperation.C.SUBMIT_EVALUATION, operationModule = PatientModule.C.PATIENT, operationDesc = "提交问诊评价")
    public Response<EvaluationResponse> submitEvaluation(
            @PathVariable Long sessionId,
            @Valid @RequestBody SubmitEvaluationRequest request,
            HttpServletRequest httpRequest) {
        Long userId = UserContext.getUserId(httpRequest);
        try {
            EvaluationResponse response = consultationEvaluationService.submitPatientEvaluation(sessionId, userId, request);
            return Response.success(response);
        } catch (Exception e) {
            return Response.fail(e.getMessage());
        }
    }

    /**
     * 上传问诊聊天文件（图片等），返回可访问的URL
     */
    @PostMapping("/consultation/upload")
    @RequirePermission("patient:api:consultation-message:create")
    @Log(operationType = PatientOperation.C.UPLOAD_CHAT_FILE, operationModule = PatientModule.C.PATIENT, operationDesc = "上传问诊聊天文件")
    public Response<FileUploadResponse> uploadConsultationFile(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest httpRequest) {
        Long userId = UserContext.getUserId(httpRequest);
        try {
            FileUploadResponse resp = localFileStorageService.upload(file, userId, "consultation", null);
            return Response.success(resp);
        } catch (Exception e) {
            return Response.fail(e.getMessage());
        }
    }

    // ==================== 诊断相关接口 ====================

    /**
     * 上传诊断图片
     */
    @PostMapping("/diagnosis/upload")
    @RequirePermission("patient:api:diagnosis:upload")
    @Log(operationType = PatientOperation.C.UPLOAD_DIAGNOSIS_IMAGE, operationModule = PatientModule.C.PATIENT, operationDesc = "上传诊断图片")
    public Response<DiagnosisRecordResponse> uploadDiagnosisImage(
            @RequestParam("image") MultipartFile image,
            @RequestParam(value = "bodyPart", required = false) String bodyPart,
            HttpServletRequest httpRequest) {
        Long userId = UserContext.getUserId(httpRequest);
        try {
            DiagnosisRecordResponse response = skinDiagnosisRecordService.uploadAndCreateRecord(userId, image, bodyPart);
            return Response.success(response);
        } catch (Exception e) {
            return Response.fail(e.getMessage());
        }
    }

    /**
     * 获取诊断记录列表
     */
    @GetMapping("/diagnosis/records")
    @RequirePermission("patient:api:skin-diagnosis-record:list")
    @Log(operationType = PatientOperation.C.QUERY_DIAGNOSIS_RECORDS, operationModule = PatientModule.C.PATIENT, operationDesc = "获取诊断记录列表")
    public Response<DiagnosisRecordListResponse> getDiagnosisRecords(
            DiagnosisRecordListRequest request,
            HttpServletRequest httpRequest) {
        Long userId = UserContext.getUserId(httpRequest);
        try {
            DiagnosisRecordListResponse response = skinDiagnosisRecordService.getPatientRecords(userId, request);
            return Response.success(response);
        } catch (Exception e) {
            return Response.fail(e.getMessage());
        }
    }

    /**
     * 获取诊断记录详情
     */
    @GetMapping("/diagnosis/records/{recordId}")
    @RequirePermission("patient:api:skin-diagnosis-record:detail")
    @Log(operationType = PatientOperation.C.QUERY_DIAGNOSIS_RECORD_DETAIL, operationModule = PatientModule.C.PATIENT, operationDesc = "获取诊断记录详情")
    public Response<DiagnosisRecordResponse> getDiagnosisRecord(
            @PathVariable Long recordId,
            HttpServletRequest httpRequest) {
        Long userId = UserContext.getUserId(httpRequest);
        try {
            DiagnosisRecordResponse response = skinDiagnosisRecordService.getRecordDetail(recordId, userId);
            return Response.success(response);
        } catch (Exception e) {
            return Response.fail(e.getMessage());
        }
    }

    /**
     * 获取诊断结果
     */
    @GetMapping("/diagnosis/records/{recordId}/results")
    @RequirePermission("patient:api:diagnosis:result")
    @Log(operationType = PatientOperation.C.QUERY_DIAGNOSIS_RESULTS, operationModule = PatientModule.C.PATIENT, operationDesc = "获取诊断结果列表")
    public Response<List<DiagnosisResultResponse>> getDiagnosisResults(
            @PathVariable Long recordId,
            HttpServletRequest httpRequest) {
        Long userId = UserContext.getUserId(httpRequest);
        try {
            List<DiagnosisResultResponse> response = diagnosisResultService.getRecordResults(recordId, userId);
            return Response.success(response);
        } catch (Exception e) {
            return Response.fail(e.getMessage());
        }
    }

    /**
     * 获取疾病类型列表
     */
    @GetMapping("/diagnosis/disease-types")
    @Log(operationType = PatientOperation.C.QUERY_DISEASE_TYPES, operationModule = PatientModule.C.PATIENT, operationDesc = "获取疾病类型列表")
    public Response<List<DiseaseTypeResponse>> getDiseaseTypes() {
        try {
            List<DiseaseTypeResponse> response = diseaseTypeService.getAllDiseaseTypes();
            return Response.success(response);
        } catch (Exception e) {
            return Response.fail(e.getMessage());
        }
    }

    /**
     * 获取可用的医生列表（用于选择医生）
     */
    @GetMapping("/consultation/doctors")
    @RequirePermission("patient:api:doctor-info:list")
    @Log(operationType = PatientOperation.C.QUERY_DOCTORS, operationModule = PatientModule.C.PATIENT, operationDesc = "获取可问诊医生列表")
    public Response<List<com.ccs.ipc.dto.patientdto.DoctorSimpleResponse>> getAvailableDoctors() {
        try {
            List<com.ccs.ipc.dto.patientdto.DoctorSimpleResponse> response = doctorInfoService.getAvailableDoctors();
            return Response.success(response);
        } catch (Exception e) {
            return Response.fail(e.getMessage());
        }
    }

    // ==================== 病知识库 ====================

    /**
     * 获取知识库分类列表（患者端，仅顶级已发布分类）
     */
    @GetMapping("/knowledge/categories")
    @RequirePermission("patient:api:knowledge:list")
    @Log(operationType = PatientOperation.C.QUERY_KNOWLEDGE_CATEGORIES, operationModule = PatientModule.C.PATIENT, operationDesc = "获取知识库分类列表")
    public Response<List<KnowledgeCategorySimple>> getKnowledgeCategories() {
        try {
            List<KnowledgeCategorySimple> response = knowledgeCategoryService.listForPatient();
            return Response.success(response);
        } catch (Exception e) {
            return Response.fail(e.getMessage());
        }
    }

    /**
     * 分页查询知识库内容（患者端，仅已发布）
     */
    @GetMapping("/knowledge/contents")
    @RequirePermission("patient:api:knowledge:list")
    @Log(operationType = PatientOperation.C.QUERY_KNOWLEDGE_CONTENTS, operationModule = PatientModule.C.PATIENT, operationDesc = "获取知识库内容列表")
    public Response<KnowledgeContentListResponse> getKnowledgeContents(KnowledgeContentListRequest request) {
        try {
            KnowledgeContentListResponse response = knowledgeContentService.listForPatient(request);
            return Response.success(response);
        } catch (Exception e) {
            return Response.fail(e.getMessage());
        }
    }

    /**
     * 获取知识库内容详情（患者端，并增加浏览次数）
     */
    @GetMapping("/knowledge/contents/{id}")
    @RequirePermission("patient:api:knowledge:detail")
    @Log(operationType = PatientOperation.C.QUERY_KNOWLEDGE_DETAIL, operationModule = PatientModule.C.PATIENT, operationDesc = "获取知识库内容详情")
    public Response<KnowledgeContentDetail> getKnowledgeContentDetail(@PathVariable Long id) {
        try {
            KnowledgeContentDetail response = knowledgeContentService.getDetailForPatient(id);
            if (response == null) {
                return Response.fail("内容不存在或已下架");
            }
            return Response.success(response);
        } catch (Exception e) {
            return Response.fail(e.getMessage());
        }
    }
}
