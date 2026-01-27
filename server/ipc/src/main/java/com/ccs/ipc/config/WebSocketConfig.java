package com.ccs.ipc.config;

import com.ccs.ipc.websocket.WebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket配置类
 * 使用STOMP协议进行消息传输
 *
 * @author WZH
 * @since 2026-01-27
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private WebSocketHandler webSocketHandler;

    /**
     * 配置消息代理
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 启用简单的消息代理，用于向客户端发送消息
        // 客户端订阅路径：/topic/xxx 或 /user/xxx
        config.enableSimpleBroker("/topic", "/user");
        // 客户端发送消息的前缀
        config.setApplicationDestinationPrefixes("/app");
        // 用户目标前缀，用于点对点消息
        config.setUserDestinationPrefix("/user");
    }

    /**
     * 注册STOMP端点
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册WebSocket端点，客户端通过此端点连接
        // 注意：WebSocket 端点需要在拦截器中排除，因为 WebSocket 有自己的认证机制
        registry.addEndpoint("/api/ws")
                .setAllowedOriginPatterns("*") // 允许跨域
                .withSockJS(); // 启用SockJS支持，提供降级方案
        
        // 同时支持原生WebSocket（不使用SockJS）
        registry.addEndpoint("/api/ws-native")
                .setAllowedOriginPatterns("*");
    }

    /**
     * 配置客户端入站通道拦截器
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketHandler);
    }
}

