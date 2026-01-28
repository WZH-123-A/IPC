import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { markAllMessagesAsRead } from '../api/doctor'
import { markAllMessagesAsRead as markAllMessagesAsReadPatient } from '../api/patient/consultation'
import { useAuthStore } from './auth'
import { useUnreadStore } from './unread'

/**
 * 聊天会话状态管理
 * 核心职责：
 * - 管理当前打开的会话ID（全局唯一状态）
 * - 统一处理已读逻辑
 */
export const useChatStore = defineStore('chat', () => {
  const authStore = useAuthStore()
  
  // 当前打开的会话ID（全局唯一状态）
  const currentSessionId = ref<number | null>(null)
  
  /**
   * 打开会话
   * 统一处理已读标记逻辑
   */
  const openSession = async (sessionId: number) => {
    if (currentSessionId.value === sessionId) {
      return // 已经是当前会话，不需要重复处理
    }
    
    currentSessionId.value = sessionId
    
    // 标记该会话的所有未读消息为已读
    const unreadStore = useUnreadStore()
    try {
      if (authStore.isDoctor) {
        await markAllMessagesAsRead(sessionId)
      } else if (authStore.isPatient) {
        await markAllMessagesAsReadPatient(sessionId)
      }
      // 清除未读数（因为已读）
      unreadStore.clear(sessionId)
    } catch (error) {
      console.error('标记消息为已读失败:', error)
    }
  }
  
  /**
   * 关闭会话
   */
  const closeSession = () => {
    currentSessionId.value = null
  }
  
  /**
   * 检查会话是否是当前打开的会话
   */
  const isCurrentSession = (sessionId: number): boolean => {
    return currentSessionId.value === sessionId
  }
  
  return {
    currentSessionId: computed(() => currentSessionId.value),
    openSession,
    closeSession,
    isCurrentSession,
  }
})

