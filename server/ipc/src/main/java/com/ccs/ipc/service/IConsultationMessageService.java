package com.ccs.ipc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ccs.ipc.dto.patientdto.ConsultationMessageListRequest;
import com.ccs.ipc.dto.patientdto.ConsultationMessageListResponse;
import com.ccs.ipc.dto.patientdto.ConsultationMessageResponse;
import com.ccs.ipc.dto.patientdto.SendMessageRequest;
import com.ccs.ipc.entity.ConsultationMessage;

/**
 * <p>
 * 问诊消息记录表 服务类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
public interface IConsultationMessageService extends IService<ConsultationMessage> {

    /**
     * 获取问诊消息列表（验证会话权限）
     *
     * @param sessionId 会话ID
     * @param userId    患者ID
     * @param request   查询请求
     * @return 消息列表响应
     */
    ConsultationMessageListResponse getSessionMessages(Long sessionId, Long userId, ConsultationMessageListRequest request);

    /**
     * 发送消息（验证会话权限）
     *
     * @param userId  患者ID
     * @param request 发送请求
     * @return 消息响应
     */
    ConsultationMessageResponse sendMessage(Long userId, SendMessageRequest request);

    /**
     * 标记消息为已读
     *
     * @param messageId 消息ID
     */
    void markAsRead(Long messageId);
}
