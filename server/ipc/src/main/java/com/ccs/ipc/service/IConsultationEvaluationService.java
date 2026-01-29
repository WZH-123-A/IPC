package com.ccs.ipc.service;

import com.ccs.ipc.dto.admindto.AdminConsultationEvaluationListRequest;
import com.ccs.ipc.dto.admindto.AdminConsultationEvaluationListResponse;
import com.ccs.ipc.dto.admindto.AdminConsultationEvaluationResponse;
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
}
