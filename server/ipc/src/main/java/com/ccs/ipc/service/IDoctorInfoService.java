package com.ccs.ipc.service;

import com.ccs.ipc.dto.doctordto.*;
import com.ccs.ipc.dto.patientdto.DoctorSimpleResponse;
import com.ccs.ipc.dto.userinfodto.AdminDoctorInfoListRequest;
import com.ccs.ipc.dto.userinfodto.AdminDoctorInfoListResponse;
import com.ccs.ipc.dto.userinfodto.AdminDoctorInfoSaveRequest;
import com.ccs.ipc.dto.userinfodto.UserOptionItem;
import com.ccs.ipc.entity.DoctorInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 医生扩展信息表 服务类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
public interface IDoctorInfoService extends IService<DoctorInfo> {

    /**
     * 获取医生首页数据
     *
     * @param doctorId 医生ID
     * @return 医生首页响应
     */
    DoctorHomeResponse getDoctorHome(Long doctorId);

    /**
     * 分页查询患者列表
     *
     * @param doctorId 医生ID
     * @param request   查询请求
     * @return 患者列表
     */
    PatientListResponse getPatientList(Long doctorId, PatientListRequest request);

    /**
     * 分页查询问诊列表
     *
     * @param doctorId 医生ID
     * @param request   查询请求
     * @return 问诊列表
     */
    ConsultationListResponse getConsultationList(Long doctorId, ConsultationListRequest request);

    /**
     * 获取可用的医生列表（用于患者选择医生）
     *
     * @return 医生列表
     */
    List<DoctorSimpleResponse> getAvailableDoctors();

    /**
     * 管理员分页查询医生信息列表（含用户名、真实姓名等）
     */
    AdminDoctorInfoListResponse getDoctorInfoListForAdmin(AdminDoctorInfoListRequest request);

    /**
     * 管理员-可选医生用户列表（已具医生角色且尚未填写医生信息的用户，用于新增时下拉）
     */
    List<UserOptionItem> getDoctorUserOptionsForAdmin();

    /**
     * 管理员新增医生信息
     */
    void createDoctorInfoForAdmin(AdminDoctorInfoSaveRequest request);

    /**
     * 管理员更新医生信息
     */
    void updateDoctorInfoForAdmin(Long id, AdminDoctorInfoSaveRequest request);

    /**
     * 管理员逻辑删除医生信息
     */
    void deleteDoctorInfoForAdmin(Long id);
}
