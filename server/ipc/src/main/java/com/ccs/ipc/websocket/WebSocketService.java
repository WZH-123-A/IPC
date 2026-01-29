package com.ccs.ipc.websocket;

import com.ccs.ipc.dto.patientdto.ConsultationMessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * WebSocket消息推送服务
 * 用于向客户端推送实时消息
 *
 * @author WZH
 * @since 2026-01-27
 */
@Slf4j
@Service
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * 向指定会话的所有参与者推送消息
     *
     * @param sessionId 会话ID
     * @param message   消息内容
     */
    public void sendMessageToSession(Long sessionId, ConsultationMessageResponse message) {
        String destination = "/topic/consultation/" + sessionId;
        messagingTemplate.convertAndSend(destination, message);
        log.debug("向会话 {} 推送消息: {}", sessionId, message.getId());
    }

    /**
     * 向指定用户推送消息（点对点）
     *
     * @param userId  用户ID
     * @param message 消息内容
     */
    public void sendMessageToUser(Long userId, ConsultationMessageResponse message) {
        String destination = "/user/" + userId + "/queue/consultation";
        messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/consultation", message);
        log.debug("向用户 {} 推送消息: {}", userId, message.getId());
    }

    /**
     * 向会话的所有参与者推送会话状态更新
     *
     * @param sessionId 会话ID
     * @param status    会话状态
     */
    public void sendSessionStatusUpdate(Long sessionId, Byte status) {
        String destination = "/topic/consultation/" + sessionId + "/status";
        messagingTemplate.convertAndSend(destination, status);
        log.debug("向会话 {} 推送状态更新: {}", sessionId, status);
    }

    /**
     * 通知指定用户刷新权限
     *
     * @param userId 被分配权限的用户ID
     */
    public void sendPermissionRefreshToUser(Long userId) {
        String destination = "/topic/permission-refresh";
        Object payload = java.util.Map.of("type", "permission-refresh", "userId", userId);
        messagingTemplate.convertAndSend(destination, payload);
    }

    /**
     * 通知多个用户刷新权限（如角色权限变更后通知该角色下所有用户）
     *
     * @param userIds 用户ID列表
     */
    public void sendPermissionRefreshToUsers(java.util.List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        for (Long userId : userIds) {
            sendPermissionRefreshToUser(userId);
        }
    }
}

