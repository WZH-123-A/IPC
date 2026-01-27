/**
 * WebSocket消息类型
 */
export interface WebSocketMessage {
  id: number
  sessionId: number
  senderId: number
  senderType: number
  messageType: number
  content: string
  aiModel?: string
  isRead: number
  createTime: string
}

/**
 * WebSocket连接状态
 */
export enum WebSocketStatus {
  DISCONNECTED = 'DISCONNECTED',
  CONNECTING = 'CONNECTING',
  CONNECTED = 'CONNECTED',
  ERROR = 'ERROR',
}

/**
 * STOMP消息帧
 */
interface StompFrame {
  command: string
  headers: Record<string, string>
  body?: string
}

/**
 * WebSocket客户端管理类
 * 使用原生WebSocket和STOMP协议
 * 
 * 注意：需要安装以下依赖：
 * npm install @stomp/stompjs sockjs-client
 * 或者使用原生WebSocket实现（当前实现）
 */
export class WebSocketClient {
  private ws: WebSocket | null = null
  private subscriptions: Map<string, (message: WebSocketMessage | number) => void> = new Map()
  private status: WebSocketStatus = WebSocketStatus.DISCONNECTED
  private reconnectAttempts = 0
  private maxReconnectAttempts = 5
  private reconnectDelay = 3000
  private statusCallbacks: Array<(status: WebSocketStatus) => void> = []
  private heartbeatInterval: number | null = null
  private subscriptionIdCounter = 0
  private subscriptionsMap: Map<string, string> = new Map() // destination -> subscriptionId

  /**
   * 连接WebSocket
   */
  connect(token: string): Promise<void> {
    return new Promise((resolve, reject) => {
      if (this.ws && this.ws.readyState === WebSocket.OPEN && this.status === WebSocketStatus.CONNECTED) {
        resolve()
        return
      }

      // 如果正在连接中，等待当前连接完成
      if (this.status === WebSocketStatus.CONNECTING) {
        const checkInterval = setInterval(() => {
          if (this.status === WebSocketStatus.CONNECTED) {
            clearInterval(checkInterval)
            resolve()
          } else if (this.status === WebSocketStatus.DISCONNECTED || this.status === WebSocketStatus.ERROR) {
            clearInterval(checkInterval)
            // 重新尝试连接
            this.connect(token).then(resolve).catch(reject)
          }
        }, 100)
        return
      }

      this.setStatus(WebSocketStatus.CONNECTING)

      // 构建 WebSocket URL
      // 优先使用 VITE_WS_BASE_URL，如果没有则从 VITE_API_BASE_URL 推导
      const wsBaseUrl = import.meta.env.VITE_WS_BASE_URL
      const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || '/api'
      let wsUrl: string
      
      if (wsBaseUrl) {
        // 如果指定了 WebSocket 基础 URL，直接使用
        if (wsBaseUrl.startsWith('ws://') || wsBaseUrl.startsWith('wss://')) {
          wsUrl = `${wsBaseUrl}/ws-native`
        } else if (wsBaseUrl.startsWith('http://')) {
          wsUrl = wsBaseUrl.replace(/^http/, 'ws') + '/ws-native'
        } else if (wsBaseUrl.startsWith('https://')) {
          wsUrl = wsBaseUrl.replace(/^https/, 'wss') + '/ws-native'
        } else {
          // 相对路径，默认使用 ws 协议
          const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
          wsUrl = `${protocol}//${wsBaseUrl.replace(/^\/+/, '')}/ws-native`
        }
      } else if (apiBaseUrl.startsWith('http://') || apiBaseUrl.startsWith('https://')) {
        // 完整 URL，直接替换协议
        wsUrl = apiBaseUrl.replace(/^https?/, (match: string) => match === 'https' ? 'wss' : 'ws') + '/ws-native'
      } else {
        // 相对路径，需要连接到后端服务器
        // 开发环境：连接到 localhost:8080
        // 生产环境：使用当前域名
        const isDev = import.meta.env.DEV
        if (isDev) {
          // 开发环境，直接连接到后端服务器
          const backendHost = import.meta.env.VITE_BACKEND_HOST || 'localhost:8080'
          wsUrl = `ws://${backendHost}/api/ws-native`
        } else {
          // 生产环境，使用当前域名
          const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
          wsUrl = `${protocol}//${window.location.host}${apiBaseUrl}/ws-native`
        }
      }

      console.log('WebSocket 连接地址:', wsUrl)
      
      let connectResolve: (() => void) | null = null
      let connectReject: ((error: Error) => void) | null = null
      let timeoutId: number | null = null

      // 连接成功回调
      const onConnected = () => {
        if (timeoutId) {
          clearTimeout(timeoutId)
          timeoutId = null
        }
        if (connectResolve) {
          connectResolve()
          connectResolve = null
          connectReject = null
        }
      }

      // 连接失败回调
      const onError = (error: Error) => {
        if (timeoutId) {
          clearTimeout(timeoutId)
          timeoutId = null
        }
        if (connectReject) {
          connectReject(error)
          connectResolve = null
          connectReject = null
        }
      }

      this.ws = new WebSocket(wsUrl)

      this.ws.onopen = () => {
        console.log('WebSocket 连接已建立，发送 STOMP CONNECT 帧')
        // 发送STOMP CONNECT帧
        const connectFrame = this.createStompFrame('CONNECT', {
          'Authorization': `Bearer ${token}`,
          'accept-version': '1.1,1.0',
          'heart-beat': '10000,10000'
        })
        this.sendStompFrame(connectFrame)
      }

      this.ws.onmessage = (event) => {
        console.log('收到 WebSocket 消息:', event.data)
        this.handleStompMessage(event.data)
      }

      this.ws.onerror = (error) => {
        console.error('WebSocket 错误:', error)
        this.setStatus(WebSocketStatus.ERROR)
        onError(new Error('WebSocket 连接失败，请检查网络或服务器状态'))
      }

      this.ws.onclose = (event) => {
        console.log('WebSocket 连接关闭:', event.code, event.reason)
        this.setStatus(WebSocketStatus.DISCONNECTED)
        this.subscriptions.clear()
        this.subscriptionsMap.clear()
        if (this.heartbeatInterval) {
          clearInterval(this.heartbeatInterval)
          this.heartbeatInterval = null
        }
        
        // 如果连接被关闭且未成功连接，触发错误
        if (this.status !== WebSocketStatus.CONNECTED && connectReject) {
          onError(new Error(`WebSocket 连接关闭: ${event.code} ${event.reason || ''}`))
        }
        
        // 尝试重连（仅在非手动关闭的情况下）
        if (this.reconnectAttempts < this.maxReconnectAttempts && event.code !== 1000) {
          this.reconnectAttempts++
          console.log(`尝试重连 WebSocket (${this.reconnectAttempts}/${this.maxReconnectAttempts})...`)
          setTimeout(() => {
            if (this.ws?.readyState !== WebSocket.OPEN) {
              this.connect(token).catch(console.error)
            }
          }, this.reconnectDelay)
        }
      }

      // 设置连接超时（15秒）
      timeoutId = window.setTimeout(() => {
        if (this.status !== WebSocketStatus.CONNECTED) {
          console.error('WebSocket 连接超时')
          if (this.ws) {
            this.ws.close()
            this.ws = null
          }
          this.setStatus(WebSocketStatus.ERROR)
          onError(new Error('WebSocket 连接超时，请检查网络连接或服务器状态'))
        }
      }, 15000)

      // 保存 resolve 和 reject
      connectResolve = () => {
        if (timeoutId) {
          clearTimeout(timeoutId)
          timeoutId = null
        }
        resolve()
      }
      connectReject = (error: Error) => {
        if (timeoutId) {
          clearTimeout(timeoutId)
          timeoutId = null
        }
        reject(error)
      }

      // 监听连接状态变化
      const statusUnsubscribe = this.onStatusChange((status) => {
        if (status === WebSocketStatus.CONNECTED) {
          statusUnsubscribe()
          onConnected()
        } else if (status === WebSocketStatus.ERROR) {
          statusUnsubscribe()
          onError(new Error('WebSocket 连接失败'))
        }
      })
    })
  }

  /**
   * 处理STOMP消息
   */
  private handleStompMessage(data: string): void {
    const frame = this.parseStompFrame(data)
    if (!frame) return

    switch (frame.command) {
      case 'CONNECTED':
        console.log('STOMP 连接成功')
        this.setStatus(WebSocketStatus.CONNECTED)
        this.reconnectAttempts = 0
        // 启动心跳
        this.startHeartbeat()
        // 重新订阅所有订阅
        this.resubscribeAll()
        break
      case 'MESSAGE':
        this.handleMessage(frame)
        break
      case 'ERROR':
        console.error('STOMP 错误:', frame.body)
        const errorMessage = frame.body || 'STOMP 连接错误'
        // 如果 token 验证失败，关闭连接
        if (errorMessage.includes('token') || errorMessage.includes('unauthorized') || errorMessage.includes('401')) {
          console.error('WebSocket 认证失败，请检查 token')
          if (this.ws) {
            this.ws.close(1008, 'Authentication failed')
          }
        }
        this.setStatus(WebSocketStatus.ERROR)
        break
    }
  }

  /**
   * 处理MESSAGE帧
   */
  private handleMessage(frame: StompFrame): void {
    const destination = frame.headers['destination']
    if (!destination) return

    const callback = this.subscriptions.get(destination)
    if (!callback) return

    try {
      // 判断是消息还是状态更新
      if (destination.includes('/status')) {
        const status = parseInt(frame.body || '0', 10)
        callback(status)
      } else {
        const message = JSON.parse(frame.body || '{}') as WebSocketMessage
        callback(message)
      }
    } catch (error) {
      console.error('解析消息失败:', error)
    }
  }

  /**
   * 重新订阅所有订阅
   */
  private resubscribeAll(): void {
    // 由于原生WebSocket实现较复杂，这里简化处理
    // 实际使用时建议使用 @stomp/stompjs 库
  }

  /**
   * 启动心跳
   */
  private startHeartbeat(): void {
    if (this.heartbeatInterval) {
      clearInterval(this.heartbeatInterval)
    }
    this.heartbeatInterval = window.setInterval(() => {
      if (this.ws && this.ws.readyState === WebSocket.OPEN) {
        this.ws.send('\n')
      }
    }, 10000)
  }

  /**
   * 创建STOMP帧
   */
  private createStompFrame(command: string, headers: Record<string, string>, body?: string): string {
    let frame = command + '\n'
    for (const [key, value] of Object.entries(headers)) {
      frame += `${key}:${value}\n`
    }
    frame += '\n'
    if (body) {
      frame += body
    }
    frame += '\0'
    return frame
  }

  /**
   * 解析STOMP帧
   */
  private parseStompFrame(data: string): StompFrame | null {
    const lines = data.split('\n')
    if (lines.length < 1) return null

    const command = lines[0]
    const headers: Record<string, string> = {}
    let bodyStartIndex = 1

    // 解析headers
    for (let i = 1; i < lines.length; i++) {
      const line = lines[i]
      if (!line) continue
      if (line === '') {
        bodyStartIndex = i + 1
        break
      }
      const colonIndex = line.indexOf(':')
      if (colonIndex > 0) {
        const key = line.substring(0, colonIndex)
        const value = line.substring(colonIndex + 1)
        headers[key] = value
      }
    }

    // 解析body
    const bodyLines = lines.slice(bodyStartIndex)
    const bodyStr = bodyLines.length > 0 ? bodyLines.join('\n').replace(/\0$/, '') : undefined

    return {
      command,
      headers,
      ...(bodyStr ? { body: bodyStr } : {})
    } as StompFrame
  }

  /**
   * 发送STOMP帧
   */
  private sendStompFrame(frame: string): void {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.ws.send(frame)
    }
  }

  /**
   * 断开连接
   */
  disconnect(): void {
    if (this.ws) {
      // 发送DISCONNECT帧
      const disconnectFrame = this.createStompFrame('DISCONNECT', {})
      this.sendStompFrame(disconnectFrame)
      this.ws.close()
      this.ws = null
    }

    this.subscriptions.clear()
    this.subscriptionsMap.clear()
    if (this.heartbeatInterval) {
      clearInterval(this.heartbeatInterval)
      this.heartbeatInterval = null
    }
    this.setStatus(WebSocketStatus.DISCONNECTED)
  }

  /**
   * 订阅会话消息
   */
  subscribeToSession(
    sessionId: number,
    callback: (message: WebSocketMessage) => void
  ): () => void {
    if (!this.ws || this.ws.readyState !== WebSocket.OPEN) {
      throw new Error('WebSocket未连接')
    }

    const destination = `/topic/consultation/${sessionId}`
    const subscriptionId = `sub-${++this.subscriptionIdCounter}`

    // 发送SUBSCRIBE帧
    const subscribeFrame = this.createStompFrame('SUBSCRIBE', {
      'id': subscriptionId,
      'destination': destination
    })
    this.sendStompFrame(subscribeFrame)

    this.subscriptions.set(destination, callback as (message: WebSocketMessage | number) => void)
    this.subscriptionsMap.set(destination, subscriptionId)

    // 返回取消订阅的函数
    return () => {
      const subId = this.subscriptionsMap.get(destination)
      if (subId) {
        const ws = this.ws
        if (ws && ws.readyState === WebSocket.OPEN) {
          const unsubscribeFrame = this.createStompFrame('UNSUBSCRIBE', {
            'id': subId
          })
          this.sendStompFrame(unsubscribeFrame)
        }
      }
      this.subscriptions.delete(destination)
      this.subscriptionsMap.delete(destination)
    }
  }

  /**
   * 订阅会话状态更新
   */
  subscribeToSessionStatus(
    sessionId: number,
    callback: (status: number) => void
  ): () => void {
    if (!this.ws || this.ws.readyState !== WebSocket.OPEN) {
      throw new Error('WebSocket未连接')
    }

    const destination = `/topic/consultation/${sessionId}/status`
    const subscriptionId = `sub-${++this.subscriptionIdCounter}`

    const subscribeFrame = this.createStompFrame('SUBSCRIBE', {
      'id': subscriptionId,
      'destination': destination
    })
    this.sendStompFrame(subscribeFrame)

    this.subscriptions.set(destination, callback as (message: WebSocketMessage | number) => void)
    this.subscriptionsMap.set(destination, subscriptionId)

    return () => {
      const subId = this.subscriptionsMap.get(destination)
      if (subId) {
        const ws = this.ws
        if (ws && ws.readyState === WebSocket.OPEN) {
          const unsubscribeFrame = this.createStompFrame('UNSUBSCRIBE', {
            'id': subId
          })
          this.sendStompFrame(unsubscribeFrame)
        }
      }
      this.subscriptions.delete(destination)
      this.subscriptionsMap.delete(destination)
    }
  }

  /**
   * 获取连接状态
   */
  getStatus(): WebSocketStatus {
    return this.status
  }

  /**
   * 是否已连接
   */
  isConnected(): boolean {
    return this.status === WebSocketStatus.CONNECTED && this.ws?.readyState === WebSocket.OPEN
  }

  /**
   * 监听状态变化
   */
  onStatusChange(callback: (status: WebSocketStatus) => void): () => void {
    this.statusCallbacks.push(callback)
    return () => {
      const index = this.statusCallbacks.indexOf(callback)
      if (index > -1) {
        this.statusCallbacks.splice(index, 1)
      }
    }
  }

  /**
   * 设置状态并通知回调
   */
  private setStatus(status: WebSocketStatus): void {
    this.status = status
    this.statusCallbacks.forEach((callback) => callback(status))
  }
}

// 创建单例实例
export const websocketClient = new WebSocketClient()

