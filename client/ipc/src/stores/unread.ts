import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getConsultationListApi } from '../api/doctor'
import { getConsultationSessions } from '../api/patient/consultation'
import { useAuthStore } from './auth'

/**
 * 未读数状态管理（单一事实源 SSOT）
 * 核心职责：
 * - 管理所有会话的未读数
 * - 提供统一的未读数查询接口
 * - 作为未读数的唯一数据源
 */
export const useUnreadStore = defineStore('unread', () => {
  const authStore = useAuthStore()
  
  // 会话未读数映射：sessionId -> unreadCount
  const sessionUnreadCounts = ref<Map<number, number>>(new Map())
  
  /**
   * 初始化：从接口加载所有会话的未读数
   */
  const initialize = async () => {
    try {
      let sessions: Array<{ id: number; unreadCount?: number }> = []
      
      if (authStore.isDoctor) {
        const response = await getConsultationListApi({ current: 1, size: 100, status: 0 })
        sessions = response.records || []
      } else if (authStore.isPatient) {
        const response = await getConsultationSessions({ current: 1, size: 100, status: 0 })
        sessions = response.data?.records || []
      }
      
      // 更新未读数映射
      sessions.forEach((session) => {
        if (session.id) {
          sessionUnreadCounts.value.set(session.id, session.unreadCount || 0)
        }
      })
    } catch (error) {
      console.error('初始化未读数失败:', error)
    }
  }
  
  /**
   * 增加会话的未读数
   */
  const increase = (sessionId: number, count: number = 1) => {
    const current = sessionUnreadCounts.value.get(sessionId) || 0
    sessionUnreadCounts.value.set(sessionId, current + count)
  }
  
  /**
   * 清除会话的未读数
   */
  const clear = (sessionId: number) => {
    sessionUnreadCounts.value.set(sessionId, 0)
  }
  
  /**
   * 设置会话的未读数
   */
  const set = (sessionId: number, count: number) => {
    sessionUnreadCounts.value.set(sessionId, count)
  }
  
  /**
   * 获取会话的未读数
   */
  const get = (sessionId: number): number => {
    return sessionUnreadCounts.value.get(sessionId) || 0
  }
  
  /**
   * 获取总未读数
   */
  const totalUnreadCount = computed(() => {
    let total = 0
    sessionUnreadCounts.value.forEach((count) => {
      total += count
    })
    return total
  })
  
  /**
   * 刷新会话的未读数（从接口重新获取）
   */
  const refreshSession = async (sessionId: number) => {
    try {
      let session: { id: number; unreadCount?: number } | null = null
      
      if (authStore.isDoctor) {
        const response = await getConsultationListApi({ current: 1, size: 100, status: 0 })
        session = response.records?.find((s) => s.id === sessionId) || null
      } else if (authStore.isPatient) {
        const response = await getConsultationSessions({ current: 1, size: 100, status: 0 })
        session = response.data?.records?.find((s) => s.id === sessionId) || null
      }
      
      if (session) {
        sessionUnreadCounts.value.set(sessionId, session.unreadCount || 0)
      }
    } catch (error) {
      console.error('刷新会话未读数失败:', error)
    }
  }
  
  /**
   * 刷新所有会话的未读数（从接口重新获取）
   */
  const refreshAll = async () => {
    await initialize()
  }
  
  return {
    // 状态
    sessionUnreadCounts: computed(() => sessionUnreadCounts.value),
    totalUnreadCount,
    
    // 方法
    initialize,
    increase,
    clear,
    set,
    get,
    refreshSession,
    refreshAll,
  }
})

