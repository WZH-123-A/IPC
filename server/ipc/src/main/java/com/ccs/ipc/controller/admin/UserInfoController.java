package com.ccs.ipc.controller.admin;

import com.ccs.ipc.common.annotation.Log;
import com.ccs.ipc.common.annotation.RequirePermission;
import com.ccs.ipc.common.enums.log.UserInfoModule;
import com.ccs.ipc.common.enums.log.UserInfoOperation;
import com.ccs.ipc.common.response.Response;
import com.ccs.ipc.dto.userinfodto.*;
import com.ccs.ipc.service.IDoctorInfoService;
import com.ccs.ipc.service.IPatientInfoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员-用户信息（医生/患者扩展信息）管理
 *
 * @author WZH
 * @since 2026-01-29
 */
@RestController
@RequestMapping("/api/admin/user-info")
public class UserInfoController {

    @Autowired
    private IDoctorInfoService doctorInfoService;
    @Autowired
    private IPatientInfoService patientInfoService;

    /**
     * 分页查询医生信息列表（含用户名、真实姓名等）
     */
    @GetMapping("/doctor/list")
    @RequirePermission("admin:api:doctor-info:list")
    @Log(operationType = UserInfoOperation.C.DOCTOR_LIST, operationModule = UserInfoModule.C.USER_INFO, operationDesc = "查询医生信息列表")
    public Response<AdminDoctorInfoListResponse> getDoctorInfoList(AdminDoctorInfoListRequest request) {
        AdminDoctorInfoListResponse response = doctorInfoService.getDoctorInfoListForAdmin(request);
        return Response.success(response);
    }

    /**
     * 可选医生用户（已具医生角色且尚未填写医生信息的用户，用于新增下拉）
     */
    @GetMapping("/doctor/user-options")
    @RequirePermission("admin:api:doctor-info:create")
    @Log(operationType = UserInfoOperation.C.DOCTOR_USER_OPTIONS, operationModule = UserInfoModule.C.USER_INFO, operationDesc = "查询医生用户选项")
    public Response<List<UserOptionItem>> getDoctorUserOptions() {
        List<UserOptionItem> list = doctorInfoService.getDoctorUserOptionsForAdmin();
        return Response.success(list);
    }

    /**
     * 新增医生信息
     */
    @PostMapping("/doctor")
    @RequirePermission("admin:api:doctor-info:create")
    @Log(operationType = UserInfoOperation.C.DOCTOR_CREATE, operationModule = UserInfoModule.C.USER_INFO, operationDesc = "新增医生信息")
    public Response<Void> createDoctorInfo(@Valid @RequestBody AdminDoctorInfoSaveRequest request) {
        doctorInfoService.createDoctorInfoForAdmin(request);
        return Response.success();
    }

    /**
     * 更新医生信息
     */
    @PutMapping("/doctor/{id}")
    @RequirePermission("admin:api:doctor-info:update")
    @Log(operationType = UserInfoOperation.C.DOCTOR_UPDATE, operationModule = UserInfoModule.C.USER_INFO, operationDesc = "更新医生信息")
    public Response<Void> updateDoctorInfo(@PathVariable Long id, @Valid @RequestBody AdminDoctorInfoSaveRequest request) {
        doctorInfoService.updateDoctorInfoForAdmin(id, request);
        return Response.success();
    }

    /**
     * 删除医生信息（逻辑删除）
     */
    @DeleteMapping("/doctor/{id}")
    @RequirePermission("admin:api:doctor-info:delete")
    @Log(operationType = UserInfoOperation.C.DOCTOR_DELETE, operationModule = UserInfoModule.C.USER_INFO, operationDesc = "删除医生信息")
    public Response<Void> deleteDoctorInfo(@PathVariable Long id) {
        doctorInfoService.deleteDoctorInfoForAdmin(id);
        return Response.success();
    }

    /**
     * 分页查询患者信息列表（含用户名、真实姓名等）
     */
    @GetMapping("/patient/list")
    @RequirePermission("admin:api:patient-info:list")
    @Log(operationType = UserInfoOperation.C.PATIENT_LIST, operationModule = UserInfoModule.C.USER_INFO, operationDesc = "查询患者信息列表")
    public Response<AdminPatientInfoListResponse> getPatientInfoList(AdminPatientInfoListRequest request) {
        AdminPatientInfoListResponse response = patientInfoService.getPatientInfoListForAdmin(request);
        return Response.success(response);
    }

    /**
     * 可选患者用户（已具患者角色且尚未填写患者信息的用户，用于新增下拉）
     */
    @GetMapping("/patient/user-options")
    @RequirePermission("admin:api:patient-info:create")
    @Log(operationType = UserInfoOperation.C.PATIENT_USER_OPTIONS, operationModule = UserInfoModule.C.USER_INFO, operationDesc = "查询患者用户选项")
    public Response<List<UserOptionItem>> getPatientUserOptions() {
        List<UserOptionItem> list = patientInfoService.getPatientUserOptionsForAdmin();
        return Response.success(list);
    }

    /**
     * 新增患者信息
     */
    @PostMapping("/patient")
    @RequirePermission("admin:api:patient-info:create")
    @Log(operationType = UserInfoOperation.C.PATIENT_CREATE, operationModule = UserInfoModule.C.USER_INFO, operationDesc = "新增患者信息")
    public Response<Void> createPatientInfo(@Valid @RequestBody AdminPatientInfoSaveRequest request) {
        patientInfoService.createPatientInfoForAdmin(request);
        return Response.success();
    }

    /**
     * 更新患者信息
     */
    @PutMapping("/patient/{id}")
    @RequirePermission("admin:api:patient-info:update")
    @Log(operationType = UserInfoOperation.C.PATIENT_UPDATE, operationModule = UserInfoModule.C.USER_INFO, operationDesc = "更新患者信息")
    public Response<Void> updatePatientInfo(@PathVariable Long id, @Valid @RequestBody AdminPatientInfoSaveRequest request) {
        patientInfoService.updatePatientInfoForAdmin(id, request);
        return Response.success();
    }

    /**
     * 删除患者信息（逻辑删除）
     */
    @DeleteMapping("/patient/{id}")
    @RequirePermission("admin:api:patient-info:delete")
    @Log(operationType = UserInfoOperation.C.PATIENT_DELETE, operationModule = UserInfoModule.C.USER_INFO, operationDesc = "删除患者信息")
    public Response<Void> deletePatientInfo(@PathVariable Long id) {
        patientInfoService.deletePatientInfoForAdmin(id);
        return Response.success();
    }
}
