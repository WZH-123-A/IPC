import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { websocketClient, type WebSocketMessage } from '../utils/websocket'
import { getConsultationListApi } from '../api/doctor'
import { getConsultationSessions } from '../api/patient/consultation'
import { useAuthStore } from './auth'
import { useChatStore } from './chat'
import { useUnreadStore } from './unread'
import { useSessionListStore } from './sessionList'
import { markAllMessagesAsRead } from '../api/doctor'
import { markAllMessagesAsRead as markAllMessagesAsReadPatient } from '../api/patient/consultation'

/**
 * 问诊消息分发中心
 * 核心思想：WebSocket 单入口 + 会话分发中心
 * - WebSocket 只订阅一次（全局订阅所有会话）
 * - 所有消息进入消息分发中心
 * - UI 组件只消费状态，不直接操作 WebSocket
 */
export const useConsultationMessageStore = defineStore('consultationMessage', () => {
  // 所有会话的消息映射：sessionId -> messages[]
  const sessionMessages = ref<Map<number, WebSocketMessage[]>>(new Map())
  
  // 会话订阅回调映射：sessionId -> callbacks[]
  const sessionCallbacks = ref<Map<number, Set<(message: WebSocketMessage) => void>>>(new Map())
  
  // 全局消息回调（用于未读数更新等）
  const globalCallbacks = ref<Set<(message: WebSocketMessage) => void>>(new Set())
  
  // WebSocket 订阅状态
  const isSubscribed = ref(false)
  
  // 已订阅的会话ID集合
  const subscribedSessions = ref<Set<number>>(new Set())
  
  /**
   * 订阅特定会话的消息
   * @param sessionId 会话ID
   * @param callback 消息回调
   * @returns 取消订阅函数
   */
  const subscribeSession = (sessionId: number, callback: (message: WebSocketMessage) => void): () => void => {
    if (!sessionCallbacks.value.has(sessionId)) {
      sessionCallbacks.value.set(sessionId, new Set())
    }
    sessionCallbacks.value.get(sessionId)!.add(callback)
    
    // 如果该会话还没有订阅 WebSocket，需要订阅
    if (!subscribedSessions.value.has(sessionId)) {
      subscribeSessionToWebSocket(sessionId)
    }
    
    // 返回取消订阅函数
    return () => {
      const callbacks = sessionCallbacks.value.get(sessionId)
      if (callbacks) {
        callbacks.delete(callback)
        // 如果没有回调了，可以取消 WebSocket 订阅（可选）
        if (callbacks.size === 0) {
          // 这里可以选择保留订阅，因为可能有其他组件需要
        }
      }
    }
  }
  
  /**
   * 订阅全局消息（用于未读数更新等）
   * @param callback 消息回调
   * @returns 取消订阅函数
   */
  const subscribeGlobal = (callback: (message: WebSocketMessage) => void): () => void => {
    globalCallbacks.value.add(callback)
    return () => {
      globalCallbacks.value.delete(callback)
    }
  }
  
  /**
   * 订阅会话到 WebSocket
   */
  const subscribeSessionToWebSocket = (sessionId: number) => {
    if (!websocketClient.isConnected()) {
      console.warn(`WebSocket未连接，无法订阅会话 ${sessionId}`)
      return
    }
    
    if (subscribedSessions.value.has(sessionId)) {
      return // 已经订阅过了
    }
    
    try {
      websocketClient.subscribeToSession(sessionId, (message: WebSocketMessage) => {
        // 统一处理消息归属、已读逻辑和未读数更新
        const chatStore = useChatStore()
        const unreadStore = useUnreadStore()
        const sessionListStore = useSessionListStore()
        const authStore = useAuthStore()
        
        // 如果消息属于当前打开的会话，立即标记为已读并清除未读数
        if (chatStore.isCurrentSession(message.sessionId)) {
          // 直接调用 API，确保新消息立即标记为已读
          if (authStore.isDoctor) {
            markAllMessagesAsRead(message.sessionId).catch(console.error)
          } else if (authStore.isPatient) {
            markAllMessagesAsReadPatient(message.sessionId).catch(console.error)
          }
          // 清除未读数（因为已读）
          unreadStore.clear(message.sessionId)
        } else {
          // 如果消息不属于当前打开的会话，增加未读数
          // 只统计接收到的消息（不是自己发送的）
          if (authStore.isDoctor) {
            // 医生端：只统计患者发送的消息（senderType === 1）
            if (message.senderType === 1) {
              unreadStore.increase(message.sessionId)
            }
          } else if (authStore.isPatient) {
            // 患者端：只统计医生和AI发送的消息（senderType === 2 或 3）
            if (message.senderType === 2 || message.senderType === 3) {
              unreadStore.increase(message.sessionId)
            }
          }
        }
        
        // 通知会话列表 store 处理新消息（事件驱动）
        sessionListStore.handleNewMessage(message)
        
        // 消息分发：通知所有订阅该会话的回调
        const callbacks = sessionCallbacks.value.get(sessionId)
        if (callbacks) {
          callbacks.forEach(cb => cb(message))
        }
        
        // 通知全局回调
        globalCallbacks.value.forEach(cb => cb(message))
        
        // 保存消息到历史记录（可选）
        if (!sessionMessages.value.has(sessionId)) {
          sessionMessages.value.set(sessionId, [])
        }
        const messages = sessionMessages.value.get(sessionId)!
        if (!messages.some(m => m.id === message.id)) {
          messages.push(message)
        }
      })
      
      subscribedSessions.value.add(sessionId)
      console.log(`已订阅会话 ${sessionId} 的 WebSocket 消息`)
    } catch (error) {
      console.error(`订阅会话 ${sessionId} 失败:`, error)
    }
  }
  
  /**
   * 初始化：订阅所有进行中的会话
   * 根据用户角色自动选择对应的API
   */
  const initialize = async () => {
    if (isSubscribed.value) {
      return // 已经初始化过了
    }
    
    if (!websocketClient.isConnected()) {
      console.warn('WebSocket未连接，无法初始化消息分发中心')
      return
    }
    
    try {
      const authStore = useAuthStore()
      const unreadStore = useUnreadStore()
      
      let sessions: Array<{ id: number; unreadCount?: number }> = []
      
      // 根据用户角色获取会话列表
      if (authStore.isDoctor) {
        // 医生端：获取所有进行中的会话
        const response = await getConsultationListApi({ current: 1, size: 100, status: 0 })
        sessions = response.records || []
      } else if (authStore.isPatient) {
        // 患者端：获取所有进行中的会话
        const response = await getConsultationSessions({ current: 1, size: 100, status: 0 })
        sessions = response.data?.records || []
      }
      
      // 同步更新未读数 store（使用已获取的会话数据，避免重复拉接口）
      sessions.forEach((session) => {
        if (session.id) {
          unreadStore.set(session.id, session.unreadCount || 0)
        }
      })
      
      // 为每个会话订阅 WebSocket
      sessions.forEach((session) => {
        subscribeSessionToWebSocket(session.id)
      })
      
      isSubscribed.value = true
      console.log('消息分发中心初始化完成，已订阅', sessions.length, '个会话')
    } catch (error) {
      console.error('初始化消息分发中心失败:', error)
    }
  }
  
  /**
   * 添加新会话到订阅列表
   */
  const addSession = (sessionId: number) => {
    subscribeSessionToWebSocket(sessionId)
  }
  
  /**
   * 获取会话的消息历史
   */
  const getSessionMessages = (sessionId: number): WebSocketMessage[] => {
    return sessionMessages.value.get(sessionId) || []
  }
  
  /**
   * 清除会话消息历史
   */
  const clearSessionMessages = (sessionId: number) => {
    sessionMessages.value.delete(sessionId)
  }
  
  return {
    // 状态
    isSubscribed,
    subscribedSessions: computed(() => subscribedSessions.value),
    
    // 方法
    subscribeSession,
    subscribeGlobal,
    initialize,
    addSession,
    getSessionMessages,
    clearSessionMessages,
  }
})

