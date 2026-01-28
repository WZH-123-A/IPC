package com.ccs.ipc.controller;

import com.ccs.ipc.common.annotation.Log;
import com.ccs.ipc.common.annotation.RequirePermission;
import com.ccs.ipc.common.enums.OperationModule;
import com.ccs.ipc.common.enums.OperationType;
import com.ccs.ipc.common.response.Response;
import com.ccs.ipc.common.util.UserContext;
import com.ccs.ipc.dto.doctordto.*;
import com.ccs.ipc.dto.patientdto.ConsultationMessageListRequest;
import com.ccs.ipc.dto.patientdto.ConsultationMessageListResponse;
import com.ccs.ipc.dto.patientdto.ConsultationMessageResponse;
import com.ccs.ipc.dto.patientdto.SendMessageRequest;
import com.ccs.ipc.service.IConsultationMessageService;
import com.ccs.ipc.service.IConsultationSessionService;
import com.ccs.ipc.service.IDoctorInfoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 医生管理控制器
 *
 * @author WZH
 * @since 2026-01-27
 */
@RestController
@RequestMapping("/api/doctor")
public class DoctorController {

    @Autowired
    private IDoctorInfoService doctorInfoService;

    @Autowired
    private IConsultationMessageService consultationMessageService;

    @Autowired
    private IConsultationSessionService consultationSessionService;

    /**
     * 获取医生首页数据
     */
    @GetMapping("/home")
    @RequirePermission("api:doctor-info:list")
    @Log(operationType = OperationType.QUERY, operationModule = OperationModule.USER, operationDesc = "获取医生首页数据")
    public Response<DoctorHomeResponse> getDoctorHome(HttpServletRequest request) {
        Long doctorId = UserContext.getUserId(request);
        DoctorHomeResponse response = doctorInfoService.getDoctorHome(doctorId);
        return Response.success(response);
    }

    /**
     * 分页查询患者列表
     */
    @GetMapping("/patients")
    @RequirePermission("api:doctor-info:list")
    @Log(operationType = OperationType.QUERY, operationModule = OperationModule.USER, operationDesc = "分页查询患者列表")
    public Response<PatientListResponse> getPatientList(PatientListRequest request, HttpServletRequest httpRequest) {
        Long doctorId = UserContext.getUserId(httpRequest);
        PatientListResponse response = doctorInfoService.getPatientList(doctorId, request);
        return Response.success(response);
    }

    /**
     * 分页查询问诊列表
     */
    @GetMapping("/consultations")
    @RequirePermission("api:doctor-info:list")
    @Log(operationType = OperationType.QUERY, operationModule = OperationModule.USER, operationDesc = "分页查询问诊列表")
    public Response<ConsultationListResponse> getConsultationList(ConsultationListRequest request, HttpServletRequest httpRequest) {
        Long doctorId = UserContext.getUserId(httpRequest);
        ConsultationListResponse response = doctorInfoService.getConsultationList(doctorId, request);
        return Response.success(response);
    }

    /**
     * 获取问诊消息列表
     */
    @GetMapping("/consultations/{sessionId}/messages")
    @RequirePermission("api:consultation-message:list")
    @Log(operationType = OperationType.QUERY, operationModule = OperationModule.USER, operationDesc = "获取问诊消息列表")
    public Response<ConsultationMessageListResponse> getConsultationMessages(
            @PathVariable Long sessionId,
            ConsultationMessageListRequest request,
            HttpServletRequest httpRequest) {
        Long doctorId = UserContext.getUserId(httpRequest);
        ConsultationMessageListResponse response = consultationMessageService.getDoctorSessionMessages(sessionId, doctorId, request);
        return Response.success(response);
    }

    /**
     * 医生发送消息
     */
    @PostMapping("/consultations/messages")
    @RequirePermission("api:consultation-message:create")
    @Log(operationType = OperationType.ADD, operationModule = OperationModule.USER, operationDesc = "医生发送消息")
    public Response<ConsultationMessageResponse> sendMessage(
            @RequestBody SendMessageRequest request,
            HttpServletRequest httpRequest) {
        Long doctorId = UserContext.getUserId(httpRequest);
        ConsultationMessageResponse response = consultationMessageService.sendDoctorMessage(doctorId, request);
        return Response.success(response);
    }

    /**
     * 结束问诊
     */
    @PostMapping("/consultations/{sessionId}/end")
    @RequirePermission("api:consultation-session:update")
    @Log(operationType = OperationType.UPDATE, operationModule = OperationModule.USER, operationDesc = "医生结束问诊")
    public Response<Void> endConsultation(
            @PathVariable Long sessionId,
            HttpServletRequest httpRequest) {
        Long doctorId = UserContext.getUserId(httpRequest);
        consultationSessionService.endSessionByDoctor(sessionId, doctorId);
        return Response.success();
    }

    /**
     * 批量标记会话的所有未读消息为已读（医生）
     */
    @PostMapping("/consultations/{sessionId}/messages/mark-all-read")
    @RequirePermission("api:consultation-message:update")
    @Log(operationType = OperationType.UPDATE, operationModule = OperationModule.USER, operationDesc = "医生批量标记消息为已读")
    public Response<Void> markAllMessagesAsRead(
            @PathVariable Long sessionId,
            HttpServletRequest httpRequest) {
        Long doctorId = UserContext.getUserId(httpRequest);
        try {
            consultationMessageService.markAllAsReadByDoctor(sessionId, doctorId);
            return Response.success();
        } catch (Exception e) {
            return Response.fail(e.getMessage());
        }
    }
}
