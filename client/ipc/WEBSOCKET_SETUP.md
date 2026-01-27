# WebSocket 实时消息功能说明

## 功能概述

已实现患者和医生问诊的实时消息推送功能，使用 WebSocket + STOMP 协议。

## 后端实现

### 1. 依赖
已在 `pom.xml` 中添加：
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

### 2. 配置类
- `WebSocketConfig.java` - WebSocket 配置，使用 STOMP 协议
- `WebSocketHandler.java` - WebSocket 连接拦截器，处理 JWT 认证
- `WebSocketService.java` - WebSocket 消息推送服务

### 3. 消息推送
在 `ConsultationMessageServiceImpl.sendMessage()` 方法中，发送消息后会自动通过 WebSocket 推送给会话的所有参与者。

## 前端实现

### 1. 安装依赖（推荐）

为了更好的兼容性和稳定性，建议安装以下依赖：

```bash
npm install @stomp/stompjs sockjs-client
```

然后修改 `src/utils/websocket.ts`，使用库版本（代码中已提供注释说明）。

### 2. 当前实现

当前使用原生 WebSocket 实现 STOMP 协议，但需要注意：

- **后端配置**：后端使用了 `.withSockJS()`，这意味着需要 SockJS 客户端
- **连接地址**：WebSocket 连接地址为 `ws://your-domain/ws` 或 `wss://your-domain/ws`
- **认证**：连接时需要在 STOMP CONNECT 帧中包含 `Authorization: Bearer <token>` 头

### 3. 使用方式

在 `ConsultationView.vue` 中已集成 WebSocket：

1. 组件挂载时自动连接 WebSocket
2. 选择会话时自动订阅该会话的消息
3. 收到消息时自动更新消息列表
4. 组件卸载时自动断开连接

### 4. WebSocket 客户端 API

```typescript
import { websocketClient } from '@/utils/websocket'

// 连接（需要token）
await websocketClient.connect(token)

// 订阅会话消息
const unsubscribe = websocketClient.subscribeToSession(sessionId, (message) => {
  console.log('收到消息:', message)
})

// 取消订阅
unsubscribe()

// 订阅状态更新
const unsubscribeStatus = websocketClient.subscribeToSessionStatus(sessionId, (status) => {
  console.log('状态更新:', status)
})

// 断开连接
websocketClient.disconnect()
```

## 消息格式

### 会话消息
```json
{
  "id": 1,
  "sessionId": 1,
  "senderId": 1,
  "senderType": 1,
  "messageType": 1,
  "content": "消息内容",
  "isRead": 0,
  "createTime": "2026-01-27T10:00:00"
}
```

### 状态更新
状态值为数字：0-进行中，1-已结束，2-已取消

## 注意事项

1. **生产环境**：建议使用 `@stomp/stompjs` 和 `sockjs-client` 库，提供更好的兼容性和错误处理
2. **HTTPS**：如果使用 HTTPS，WebSocket 需要使用 WSS 协议
3. **重连机制**：已实现自动重连，最多重试 5 次
4. **心跳**：已实现心跳机制，保持连接活跃

## 测试

1. 打开问诊页面
2. 创建或选择一个会话
3. 发送消息
4. 消息会实时显示，无需刷新页面

