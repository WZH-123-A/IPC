<template>
  <div class="consultation-chat-view">
    <!-- 顶部标题栏 -->
    <div class="view-header">
      <div class="header-content">
        <h2>在线问诊</h2>
        <p class="subtitle">实时回复患者问诊，提供专业医疗建议</p>
      </div>
      <div class="header-stats">
        <el-statistic title="今日问诊" :value="stats.todayCount" />
        <el-statistic title="进行中" :value="stats.ongoingCount" />
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="main-container">
      <!-- 左侧会话列表 -->
      <div class="sessions-sidebar">
        <div class="sidebar-header">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索患者或问诊标题..."
            clearable
            prefix-icon="Search"
            @input="handleSearch"
          />
        </div>
        <div class="sessions-list">
          <div v-if="loading" class="loading-wrapper">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
          <div v-else-if="sessions.length === 0" class="empty-sessions">
            <el-empty description="暂无问诊会话" :image-size="100" />
          </div>
          <div
            v-for="session in filteredSessions"
            :key="session.id"
            :class="['session-item', { active: currentSessionId === session.id }]"
            @click="selectSession(session.id)"
          >
            <el-avatar :src="(session as Consultation).patientAvatar" :size="48" class="session-avatar">
              <el-icon><User /></el-icon>
            </el-avatar>
            <div class="session-info">
              <div class="session-header">
                <span class="patient-name">{{ (session as Consultation).patientName || '患者' }}</span>
                <span class="session-time">{{ formatTime(session.createTime) }}</span>
              </div>
              <div class="session-title">{{ session.title || '问诊会话' }}</div>
              <div class="session-footer">
                <el-tag :type="getStatusType(session.status)" size="small">
                  {{ getStatusText(session.status) }}
                </el-tag>
                <el-badge
                  v-if="getSessionUnreadCount(session.id) > 0"
                  :value="getSessionUnreadCount(session.id)"
                />
              </div>
            </div>
          </div>
        </div>
        <!-- 分页 -->
        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="pagination.current"
            v-model:page-size="pagination.size"
            :total="pagination.total"
            :page-sizes="[10, 20, 50]"
            layout="prev, pager, next"
            small
            @size-change="(size: number) => { sessionListStore.setPagination(pagination.current, size); loadSessions() }"
            @current-change="(current: number) => { sessionListStore.setPagination(current, pagination.size); loadSessions() }"
          />
        </div>
      </div>

      <!-- 右侧聊天区域 -->
      <div class="chat-main">
        <div v-if="!currentSession" class="empty-chat">
          <el-empty description="请选择一个问诊会话开始对话" :image-size="200" />
        </div>
        <ConsultationChat
          v-else
          :session="currentSession"
          :doctor-avatar="authStore.userInfo?.avatar"
          @message-sent="handleMessageSent"
          @session-ended="handleSessionEnded"
          @messages-read="handleMessagesRead"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Loading } from '@element-plus/icons-vue'
import { useAuthStore } from '../../stores/auth'
import { useChatStore } from '../../stores/chat'
import { useSessionListStore } from '../../stores/sessionList'
import { useUnreadStore } from '../../stores/unread'
import { useConsultationMessageStore } from '../../stores/consultationMessage'
import type { Consultation } from '../../api/doctor'
import ConsultationChat from '../../components/doctor/ConsultationChat.vue'
import type { WebSocketMessage } from '../../utils/websocket'

const authStore = useAuthStore()
const chatStore = useChatStore()
const sessionListStore = useSessionListStore()
const unreadStore = useUnreadStore()
const messageStore = useConsultationMessageStore()

const currentSessionId = ref<number | null>(null)

// 定义聊天组件需要的会话类型
type ChatSession = {
  id: number
  patientId: number
  patientName?: string
  patientAvatar?: string
  title?: string
  status: number
}

const currentSession = ref<ChatSession | null>(null)
const searchKeyword = ref('')

// 从 store 读取会话列表和状态
const sessions = computed(() => sessionListStore.sessions)
const loading = computed(() => sessionListStore.loading)
const pagination = computed(() => sessionListStore.pagination)
const stats = computed(() => sessionListStore.stats)

// 过滤后的会话列表
const filteredSessions = computed(() => {
  if (!searchKeyword.value) return sessions.value

  const keyword = searchKeyword.value.toLowerCase()
  return sessions.value.filter((s) => {
    const consultation = s as Consultation
    return (
      consultation.patientName?.toLowerCase().includes(keyword) ||
      consultation.title?.toLowerCase().includes(keyword) ||
      consultation.sessionNo?.toLowerCase().includes(keyword)
    )
  })
})

// 格式化时间
const formatTime = (time: string) => {
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const minutes = Math.floor(diff / 60000)

  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (minutes < 1440) return `${Math.floor(minutes / 60)}小时前`

  return date.toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

const getStatusText = (status: number) => {
  const statusMap: Record<number, string> = {
    0: '进行中',
    1: '已结束',
    2: '已取消',
  }
  return statusMap[status] || '未知'
}

const getStatusType = (status: number): 'success' | 'warning' | 'danger' | 'info' => {
  const typeMap: Record<number, 'success' | 'warning' | 'danger' | 'info'> = {
    0: 'warning',
    1: 'success',
    2: 'danger',
  }
  return typeMap[status] || 'info'
}

// 加载会话列表（从 store 调用）
const loadSessions = async () => {
  try {
    await sessionListStore.loadSessions()
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : '加载问诊列表失败'
    ElMessage.error(message)
  }
}

// 选择会话
const selectSession = (sessionId: number) => {
  currentSessionId.value = sessionId
  const session = sessions.value.find((s) => s.id === sessionId) as Consultation | undefined
  if (session) {
    currentSession.value = {
      id: session.id,
      patientId: session.patientId,
      patientName: session.patientName,
      patientAvatar: session.patientAvatar,
      title: session.title,
      status: session.status,
    }
  }
}

// 获取会话的未读数（从 UnreadStore 读取）
const getSessionUnreadCount = (sessionId: number): number => {
  return unreadStore.get(sessionId)
}

// 搜索
const handleSearch = () => {
  // 搜索在 computed 中自动处理
}

// 消息发送后处理（事件驱动，不需要手动刷新）
const handleMessageSent = () => {
  // 消息已通过 WebSocket 实时更新，不需要手动刷新列表
}

// 会话结束后处理（事件驱动）
const handleSessionEnded = () => {
  if (currentSession.value) {
    currentSession.value.status = 1
    // 更新 store 中的会话状态
    sessionListStore.updateSessionStatus(currentSession.value.id, 1)
  }
}

// 消息标记为已读后处理（事件驱动，不需要手动刷新）
const handleMessagesRead = () => {
  // 未读数已通过 UnreadStore 实时更新，不需要手动刷新列表
}

// 消息订阅取消函数
let unsubscribeGlobalMessages: (() => void) | null = null

onMounted(async () => {
  // 初始加载会话列表
  await loadSessions()

  // 订阅全局消息，响应消息变化自动更新列表
  unsubscribeGlobalMessages = messageStore.subscribeGlobal((message: WebSocketMessage) => {
    // 处理新消息，更新会话列表
    sessionListStore.handleNewMessage(message)
  })

  // 检查URL参数，如果有sessionId则自动选择
  const urlParams = new URLSearchParams(window.location.search)
  const sessionId = urlParams.get('sessionId')
  if (sessionId) {
    selectSession(Number(sessionId))
  }
})

onUnmounted(() => {
  if (unsubscribeGlobalMessages) {
    unsubscribeGlobalMessages()
    unsubscribeGlobalMessages = null
  }
  // 关闭当前会话
  chatStore.closeSession()
})
</script>

<style scoped>
.consultation-chat-view {
  height: calc(100vh - 60px);
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

.view-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-content h2 {
  margin: 0 0 4px 0;
  font-size: 24px;
  font-weight: 600;
}

.subtitle {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
}

.header-stats {
  display: flex;
  gap: 32px;
}

.header-stats :deep(.el-statistic__head) {
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
}

.header-stats :deep(.el-statistic__number) {
  color: #fff;
  font-size: 24px;
  font-weight: 600;
}

.main-container {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.sessions-sidebar {
  width: 360px;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-right: 1px solid #e4e7ed;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
}

.sidebar-header {
  padding: 16px;
  border-bottom: 1px solid #e4e7ed;
}

.sessions-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.loading-wrapper,
.empty-sessions {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #909399;
  gap: 8px;
}

.session-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  margin-bottom: 8px;
}

.session-item:hover {
  background: #f5f7fa;
}

.session-item.active {
  background: #ecf5ff;
  border: 1px solid #b3d8ff;
}

.session-avatar {
  flex-shrink: 0;
}

.session-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.session-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.patient-name {
  font-weight: 600;
  color: #303133;
  font-size: 14px;
}

.session-time {
  font-size: 12px;
  color: #909399;
}

.session-title {
  font-size: 13px;
  color: #606266;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.session-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination-wrapper {
  padding: 12px;
  border-top: 1px solid #e4e7ed;
  display: flex;
  justify-content: center;
}

.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #fff;
}

.empty-chat {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
