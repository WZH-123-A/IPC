package com.ccs.ipc.websocket;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 在线用户注册表
 * 基于 WebSocket 连接/断开维护当前在线用户数（按唯一用户去重）
 *
 * @author WZH
 * @since 2026-01-29
 */
@Component
public class OnlineUserRegistry {

    /** sessionId -> userId，同一用户多端连接只按用户去重统计 */
    private final ConcurrentHashMap<String, Long> sessionToUser = new ConcurrentHashMap<>();

    /**
     * 注册连接：记录该会话对应的用户
     *
     * @param sessionId STOMP 会话 ID
     * @param userId    用户 ID
     */
    public void addSession(String sessionId, Long userId) {
        if (sessionId != null && userId != null) {
            sessionToUser.put(sessionId, userId);
        }
    }

    /**
     * 移除连接
     *
     * @param sessionId STOMP 会话 ID
     */
    public void removeSession(String sessionId) {
        if (sessionId != null) {
            sessionToUser.remove(sessionId);
        }
    }

    /**
     * 当前在线人数（去重后的用户数）
     */
    public int getOnlineCount() {
        return (int) sessionToUser.values().stream().distinct().count();
    }
}
