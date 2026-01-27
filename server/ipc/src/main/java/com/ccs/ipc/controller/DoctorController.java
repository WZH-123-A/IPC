package com.ccs.ipc.controller;

import com.ccs.ipc.common.annotation.Log;
import com.ccs.ipc.common.annotation.RequirePermission;
import com.ccs.ipc.common.enums.OperationModule;
import com.ccs.ipc.common.enums.OperationType;
import com.ccs.ipc.common.response.Response;
import com.ccs.ipc.common.util.UserContext;
import com.ccs.ipc.dto.doctordto.*;
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

    /**
     * 获取医生首页数据
     */
    @GetMapping("/home")
    @RequirePermission("api:doctor:home")
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
    @RequirePermission("api:doctor:patients")
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
    @RequirePermission("api:doctor:consultation")
    @Log(operationType = OperationType.QUERY, operationModule = OperationModule.USER, operationDesc = "分页查询问诊列表")
    public Response<ConsultationListResponse> getConsultationList(ConsultationListRequest request, HttpServletRequest httpRequest) {
        Long doctorId = UserContext.getUserId(httpRequest);
        ConsultationListResponse response = doctorInfoService.getConsultationList(doctorId, request);
        return Response.success(response);
    }
}
