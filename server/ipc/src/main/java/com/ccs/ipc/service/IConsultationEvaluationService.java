package com.ccs.ipc.service;

import com.ccs.ipc.dto.admindto.AdminConsultationEvaluationListRequest;
import com.ccs.ipc.dto.admindto.AdminConsultationEvaluationListResponse;
import com.ccs.ipc.dto.admindto.AdminConsultationEvaluationResponse;
import com.ccs.ipc.dto.patientdto.EvaluationResponse;
import com.ccs.ipc.dto.patientdto.SubmitEvaluationRequest;
import com.ccs.ipc.entity.ConsultationEvaluation;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 问诊评价表 服务类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
public interface IConsultationEvaluationService extends IService<ConsultationEvaluation> {

    /**
     * 管理员分页查询问诊评价列表
     *
     * @param request 查询请求
     * @return 评价列表响应
     */
    AdminConsultationEvaluationListResponse getAdminEvaluationList(AdminConsultationEvaluationListRequest request);

    /**
     * 管理员新增问诊评价
     */
    AdminConsultationEvaluationResponse createAdminEvaluation(com.ccs.ipc.dto.admindto.AdminConsultationEvaluationCreateRequest request);

    /**
     * 管理员更新问诊评价
     */
    void updateAdminEvaluation(Long id, com.ccs.ipc.dto.admindto.AdminConsultationEvaluationUpdateRequest request);

    /**
     * 管理员逻辑删除问诊评价
     */
    void deleteAdminEvaluation(Long id);

    /**
     * 患者提交问诊评价（会话结束后、每个会话仅可评价一次）
     */
    EvaluationResponse submitPatientEvaluation(Long sessionId, Long patientId, SubmitEvaluationRequest request);

    /**
     * 根据会话ID查询评价（患者端，用于展示是否已评价）
     */
    EvaluationResponse getBySessionId(Long sessionId);
}
