package com.ccs.ipc.controller;

import com.ccs.ipc.common.annotation.Log;
import com.ccs.ipc.common.annotation.RequirePermission;
import com.ccs.ipc.common.enums.OperationModule;
import com.ccs.ipc.common.enums.OperationType;
import com.ccs.ipc.common.response.Response;
import com.ccs.ipc.dto.admindto.*;
import com.ccs.ipc.dto.patientdto.ConsultationMessageListRequest;
import com.ccs.ipc.dto.patientdto.ConsultationMessageListResponse;
import com.ccs.ipc.service.IConsultationEvaluationService;
import com.ccs.ipc.service.IConsultationMessageService;
import com.ccs.ipc.service.IConsultationSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员端问诊会话管理控制器
 * 统一管理问诊会话相关接口，通过权限注解控制访问
 *
 * @author WZH
 * @since 2026-01-28
 */
@RestController
@RequestMapping("/api/admin/consultation")
public class ConsultationController {

    @Autowired
    private IConsultationSessionService consultationSessionService;

    @Autowired
    private IConsultationMessageService consultationMessageService;

    @Autowired
    private IConsultationEvaluationService consultationEvaluationService;

    /**
     * 分页查询问诊会话列表
     */
    @GetMapping("/sessions/list")
    @RequirePermission("api:consultation-session:list")
    @Log(operationType = OperationType.QUERY, operationModule = OperationModule.CONSULTATION, operationDesc = "分页查询问诊会话列表")
    public Response<AdminConsultationSessionListResponse> getSessionList(AdminConsultationSessionListRequest request) {
        AdminConsultationSessionListResponse response = consultationSessionService.getAdminSessionList(request);
        return Response.success(response);
    }

    /**
     * 根据ID获取问诊会话详情
     */
    @GetMapping("/sessions/{id}")
    @RequirePermission("api:consultation-session:detail")
    @Log(operationType = OperationType.QUERY, operationModule = OperationModule.CONSULTATION, operationDesc = "根据ID获取问诊会话详情")
    public Response<AdminConsultationSessionResponse> getSessionById(@PathVariable Long id) {
        AdminConsultationSessionResponse response = consultationSessionService.getAdminSessionById(id);
        return Response.success(response);
    }

    /**
     * 管理员分页查询指定会话的问诊消息列表
     */
    @GetMapping("/sessions/{id}/messages")
    @RequirePermission("api:consultation-message:list")
    @Log(operationType = OperationType.QUERY, operationModule = OperationModule.CONSULTATION, operationDesc = "管理员分页查询问诊消息")
    public Response<ConsultationMessageListResponse> getSessionMessages(
            @PathVariable Long id,
            ConsultationMessageListRequest request) {
        ConsultationMessageListResponse response = consultationMessageService.getAdminSessionMessages(id, request);
        return Response.success(response);
    }

    /**
     * 管理员分页查询问诊评价列表
     */
    @GetMapping("/evaluations/list")
    @RequirePermission("api:consultation-evaluation:list")
    @Log(operationType = OperationType.QUERY, operationModule = OperationModule.CONSULTATION, operationDesc = "分页查询问诊评价列表")
    public Response<AdminConsultationEvaluationListResponse> getEvaluationList(AdminConsultationEvaluationListRequest request) {
        AdminConsultationEvaluationListResponse response = consultationEvaluationService.getAdminEvaluationList(request);
        return Response.success(response);
    }
}

