package com.ccs.ipc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ccs.ipc.dto.patientdto.ConsultationSessionListRequest;
import com.ccs.ipc.dto.patientdto.ConsultationSessionListResponse;
import com.ccs.ipc.dto.patientdto.ConsultationSessionResponse;
import com.ccs.ipc.dto.patientdto.CreateConsultationSessionRequest;
import com.ccs.ipc.entity.ConsultationSession;

/**
 * <p>
 * 在线问诊会话表 服务类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
public interface IConsultationSessionService extends IService<ConsultationSession> {

    /**
     * 获取患者的问诊会话列表
     *
     * @param userId  患者ID
     * @param request 查询请求
     * @return 会话列表响应
     */
    ConsultationSessionListResponse getPatientSessions(Long userId, ConsultationSessionListRequest request);

    /**
     * 获取问诊会话详情（验证权限）
     *
     * @param sessionId 会话ID
     * @param userId    患者ID
     * @return 会话响应
     */
    ConsultationSessionResponse getSessionDetail(Long sessionId, Long userId);

    /**
     * 创建问诊会话
     *
     * @param userId  患者ID
     * @param request 创建请求
     * @return 会话响应
     */
    ConsultationSessionResponse createSession(Long userId, CreateConsultationSessionRequest request);

    /**
     * 结束问诊会话（验证权限）
     *
     * @param sessionId 会话ID
     * @param userId    患者ID
     */
    void endSession(Long sessionId, Long userId);
}
