package com.ccs.ipc.service;

import com.ccs.ipc.dto.userinfodto.AdminPatientInfoListRequest;
import com.ccs.ipc.dto.userinfodto.AdminPatientInfoListResponse;
import com.ccs.ipc.dto.userinfodto.AdminPatientInfoSaveRequest;
import com.ccs.ipc.dto.userinfodto.UserOptionItem;
import com.ccs.ipc.entity.PatientInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 患者扩展信息表 服务类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
public interface IPatientInfoService extends IService<PatientInfo> {

    /**
     * 管理员分页查询患者信息列表（含用户名、真实姓名等）
     */
    AdminPatientInfoListResponse getPatientInfoListForAdmin(AdminPatientInfoListRequest request);

    /**
     * 管理员-可选患者用户列表（已具患者角色且尚未填写患者信息的用户，用于新增时下拉）
     */
    List<UserOptionItem> getPatientUserOptionsForAdmin();

    /**
     * 管理员新增患者信息
     */
    void createPatientInfoForAdmin(AdminPatientInfoSaveRequest request);

    /**
     * 管理员更新患者信息
     */
    void updatePatientInfoForAdmin(Long id, AdminPatientInfoSaveRequest request);

    /**
     * 管理员逻辑删除患者信息
     */
    void deletePatientInfoForAdmin(Long id);
}
