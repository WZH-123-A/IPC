package com.ccs.ipc.websocket;

import com.ccs.ipc.common.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;

/**
 * WebSocket消息拦截器
 * 用于处理连接、断开连接和消息发送时的逻辑
 *
 * @author WZH
 * @since 2026-01-27
 */
@Slf4j
@Component
public class WebSocketHandler implements ChannelInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null) {
            StompCommand command = accessor.getCommand();
            
            if (StompCommand.CONNECT.equals(command)) {
                log.info("收到 WebSocket CONNECT 请求");
                // 处理连接请求，验证token
                List<String> tokenList = accessor.getNativeHeader("Authorization");
                if (tokenList != null && !tokenList.isEmpty()) {
                    String token = tokenList.get(0);
                    log.debug("Authorization header: {}", token != null && token.length() > 20 ? token.substring(0, 20) + "..." : token);
                    if (token != null && token.startsWith("Bearer ")) {
                        token = token.substring(7);
                        try {
                            // 验证JWT token
                            Long userId = jwtUtil.getUserIdFromToken(token);
                            boolean isValid = jwtUtil.validateToken(token);
                            log.debug("Token验证结果 - userId: {}, isValid: {}", userId, isValid);
                            
                            if (userId != null && isValid) {
                                // 设置用户身份
                                Principal principal = () -> userId.toString();
                                accessor.setUser(principal);
                                log.info("WebSocket连接成功，用户ID: {}", userId);
                            } else {
                                log.warn("WebSocket连接失败，无效的token - userId: {}, isValid: {}", userId, isValid);
                                // 拒绝连接 - 设置错误消息
                                accessor.setLeaveMutable(true);
                                throw new RuntimeException("Invalid token");
                            }
                        } catch (RuntimeException e) {
                            log.error("WebSocket连接验证失败: {}", e.getMessage());
                            // 重新抛出异常以拒绝连接
                            throw e;
                        } catch (Exception e) {
                            log.error("WebSocket连接验证异常", e);
                            // 拒绝连接
                            throw new RuntimeException("Token validation failed: " + e.getMessage());
                        }
                    } else {
                        log.warn("WebSocket连接失败，缺少Bearer token前缀");
                        throw new RuntimeException("Missing Bearer token");
                    }
                } else {
                    log.warn("WebSocket连接失败，缺少Authorization头");
                    throw new RuntimeException("Missing Authorization header");
                }
            } else if (StompCommand.DISCONNECT.equals(command)) {
                // 处理断开连接
                Principal user = accessor.getUser();
                if (user != null) {
                    log.info("WebSocket断开连接，用户ID: {}", user.getName());
                } else {
                    log.info("WebSocket断开连接（未认证用户）");
                }
            }
        }
        
        return message;
    }
}

