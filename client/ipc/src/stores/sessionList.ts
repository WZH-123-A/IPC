import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getConsultationListApi, type Consultation, type ConsultationListParams } from '../api/doctor'
import { getConsultationSessions, type ConsultationSession } from '../api/patient/consultation'
import { useAuthStore } from './auth'
import { useUnreadStore } from './unread'
import type { WebSocketMessage } from '../utils/websocket'

// 统一的会话类型（包含共同字段）
type SessionItem = Consultation | ConsultationSession

/**
 * 会话列表状态管理
 * 核心职责：
 * - 管理会话列表数据
 * - 响应消息事件自动更新列表
 * - 提供统一的会话列表查询接口
 */
export const useSessionListStore = defineStore('sessionList', () => {
  const authStore = useAuthStore()
  const unreadStore = useUnreadStore()
  
  // 会话列表
  const sessions = ref<SessionItem[]>([])
  const loading = ref(false)
  
  // 分页信息
  const paginationState = ref({
    current: 1,
    size: 20,
    total: 0,
  })
  
  const pagination = computed(() => paginationState.value)
  
  /**
   * 设置分页信息
   */
  const setPagination = (current: number, size: number) => {
    paginationState.value.current = current
    paginationState.value.size = size
  }
  
  /**
   * 加载会话列表
   */
  const loadSessions = async (params?: Partial<ConsultationListParams>) => {
    loading.value = true
    try {
      const requestParams: ConsultationListParams = {
        current: paginationState.value.current,
        size: paginationState.value.size,
        ...params,
      }
      
      if (authStore.isDoctor) {
        const response = await getConsultationListApi(requestParams)
        sessions.value = response.records || []
        paginationState.value.total = response.total || 0
        paginationState.value.current = response.current || 1
        paginationState.value.size = response.size || 20
        
        // 同步更新未读数 store
        response.records?.forEach((session) => {
          if (session.id) {
            unreadStore.set(session.id, session.unreadCount || 0)
          }
        })
      } else if (authStore.isPatient) {
        const response = await getConsultationSessions(requestParams)
        const records = response.data?.records || []
        sessions.value = records
        paginationState.value.total = response.data?.total || 0
        paginationState.value.current = response.data?.current || 1
        paginationState.value.size = response.data?.size || 20
        
        // 同步更新未读数 store
        // ConsultationSession 类型没有 unreadCount，但从接口返回的数据可能有
        records.forEach((session) => {
          if (session.id) {
            const sessionWithUnread = session as ConsultationSession & { unreadCount?: number }
            unreadStore.set(session.id, sessionWithUnread.unreadCount || 0)
          }
        })
      }
    } catch (error) {
      console.error('加载会话列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }
  
  /**
   * 更新会话状态（响应会话结束等事件）
   */
  const updateSessionStatus = (sessionId: number, status: number) => {
    const session = sessions.value.find((s) => s.id === sessionId)
    if (session) {
      session.status = status
    }
  }
  
  /**
   * 处理新消息（更新会话列表中的最后消息时间等）
   */
  const handleNewMessage = (message: WebSocketMessage) => {
    const session = sessions.value.find((s) => s.id === message.sessionId)
    if (session) {
      // 可以更新最后消息时间等字段
      // 这里暂时不更新，因为后端接口可能已经包含了这些信息
    }
  }
  
  /**
   * 获取统计信息
   */
  const stats = computed(() => {
    const today = new Date()
    const todayCount = sessions.value.filter((s) => {
      const createTime = new Date(s.createTime)
      return createTime.toDateString() === today.toDateString()
    }).length
    
    const ongoingCount = sessions.value.filter((s) => s.status === 0).length
    
    return {
      todayCount,
      ongoingCount,
    }
  })
  
  return {
    // 状态
    sessions: computed(() => sessions.value),
    loading: computed(() => loading.value),
    pagination,
    stats,
    
    // 方法
    loadSessions,
    setPagination,
    updateSessionStatus,
    handleNewMessage,
  }
})

