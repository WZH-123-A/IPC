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
            <el-avatar :src="session.patientAvatar" :size="48" class="session-avatar">
              <el-icon><User /></el-icon>
            </el-avatar>
            <div class="session-info">
              <div class="session-header">
                <span class="patient-name">{{ session.patientName || '患者' }}</span>
                <span class="session-time">{{ formatTime(session.createTime) }}</span>
              </div>
              <div class="session-title">{{ session.title || '问诊会话' }}</div>
              <div class="session-footer">
                <el-tag :type="getStatusType(session.status)" size="small">
                  {{ getStatusText(session.status) }}
                </el-tag>
                <el-badge v-if="session.unreadCount && session.unreadCount > 0" :value="session.unreadCount" />
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
            @size-change="loadSessions"
            @current-change="loadSessions"
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
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Loading, Search } from '@element-plus/icons-vue'
import { useAuthStore } from '../../stores/auth'
import { getConsultationListApi, type Consultation, type ConsultationListParams } from '../../api/doctor'
import ConsultationChat from '../../components/doctor/ConsultationChat.vue'
import { websocketClient, WebSocketStatus } from '../../utils/websocket'

const authStore = useAuthStore()

const sessions = ref<Consultation[]>([])
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
const loading = ref(false)
const searchKeyword = ref('')
const pagination = reactive({
  current: 1,
  size: 20,
  total: 0,
})

const stats = reactive({
  todayCount: 0,
  ongoingCount: 0,
})

// 过滤后的会话列表
const filteredSessions = computed(() => {
  if (!searchKeyword.value) return sessions.value
  
  const keyword = searchKeyword.value.toLowerCase()
  return sessions.value.filter(
    (s) =>
      s.patientName?.toLowerCase().includes(keyword) ||
      s.title?.toLowerCase().includes(keyword) ||
      s.sessionNo?.toLowerCase().includes(keyword)
  )
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

// 加载会话列表
const loadSessions = async () => {
  loading.value = true
  try {
    const params: ConsultationListParams = {
      current: pagination.current,
      size: pagination.size,
    }
    const response = await getConsultationListApi(params)
    sessions.value = response.records || []
    pagination.total = response.total || 0
    pagination.current = response.current || 1
    pagination.size = response.size || 20
    
    // 更新统计
    stats.todayCount = sessions.value.filter((s) => {
      const today = new Date()
      const createTime = new Date(s.createTime)
      return createTime.toDateString() === today.toDateString()
    }).length
    stats.ongoingCount = sessions.value.filter((s) => s.status === 0).length
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : '加载问诊列表失败'
    ElMessage.error(message)
  } finally {
    loading.value = false
  }
}

// 选择会话
const selectSession = (sessionId: number) => {
  currentSessionId.value = sessionId
  const session = sessions.value.find((s) => s.id === sessionId)
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

// 搜索
const handleSearch = () => {
  // 搜索在 computed 中自动处理
}

// 消息发送后刷新
const handleMessageSent = () => {
  loadSessions()
}

// 会话结束后刷新
const handleSessionEnded = () => {
  loadSessions()
  if (currentSession.value) {
    currentSession.value.status = 1
  }
}

// WebSocket状态监听
let unsubscribeStatusChange: (() => void) | null = null

onMounted(async () => {
  // 连接WebSocket
  if (authStore.token) {
    try {
      await websocketClient.connect(authStore.token)
    } catch (error: unknown) {
      const message = error instanceof Error ? error.message : 'WebSocket连接失败'
      console.error(message)
    }
  }
  
  // 监听WebSocket状态
  unsubscribeStatusChange = websocketClient.onStatusChange((status) => {
    if (status === WebSocketStatus.CONNECTED) {
      ElMessage.success('实时消息连接成功')
    }
  })
  
  await loadSessions()
  
  // 检查URL参数，如果有sessionId则自动选择
  const urlParams = new URLSearchParams(window.location.search)
  const sessionId = urlParams.get('sessionId')
  if (sessionId) {
    selectSession(Number(sessionId))
  }
})

onUnmounted(() => {
  if (unsubscribeStatusChange) {
    unsubscribeStatusChange()
    unsubscribeStatusChange = null
  }
  websocketClient.disconnect()
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

