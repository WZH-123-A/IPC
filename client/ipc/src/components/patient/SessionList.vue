<template>
  <div class="session-list-panel">
    <div class="panel-header">
      <h3>我的问诊</h3>
      <el-input
        v-model="searchKeyword"
        placeholder="搜索问诊..."
        clearable
        prefix-icon="Search"
        class="search-input"
      />
    </div>
    <div class="session-list">
      <div
        v-for="session in filteredSessions"
        :key="session.id"
        class="session-item"
        :class="{ active: currentSessionId === session.id }"
        @click="$emit('select', session.id)"
      >
        <div class="session-avatar">
          <el-icon :size="24" :class="session.sessionType === 1 ? 'ai-icon' : 'doctor-icon'">
            <component :is="session.sessionType === 1 ? 'ChatDotRound' : 'UserFilled'" />
          </el-icon>
        </div>
        <div class="session-info">
          <div class="session-title">{{ session.title || '未命名问诊' }}</div>
          <div class="session-meta">
            <span class="session-type">{{ session.sessionType === 1 ? 'AI问诊' : '医生问诊' }}</span>
            <el-divider direction="vertical" />
            <span class="session-time">{{ formatTime(session.createTime) }}</span>
          </div>
        </div>
        <div class="session-status">
          <el-tag :type="getStatusType(session.status)" size="small">
            {{ getStatusText(session.status) }}
          </el-tag>
          <el-badge
            v-if="getSessionUnreadCount(session.id) > 0"
            :value="getSessionUnreadCount(session.id)"
            class="session-unread-badge"
          />
        </div>
      </div>
      <div v-if="filteredSessions.length === 0" class="empty-sessions">
        <el-empty description="暂无问诊记录" :image-size="120">
          <el-button type="primary" @click="$emit('create')">开始问诊</el-button>
        </el-empty>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ChatDotRound, UserFilled } from '@element-plus/icons-vue'
import type { ConsultationSession } from '../../api/patient/consultation'
import { useUnreadStore } from '../../stores/unread'

interface Props {
  sessions: ConsultationSession[]
  currentSessionId: number | null
}

const props = defineProps<Props>()
const unreadStore = useUnreadStore()

// 获取会话的未读数（从 UnreadStore 读取）
const getSessionUnreadCount = (sessionId: number): number => {
  return unreadStore.get(sessionId)
}

defineEmits<{
  select: [sessionId: number]
  create: []
}>()

const searchKeyword = ref('')

const filteredSessions = computed(() => {
  if (!searchKeyword.value) {
    return props.sessions
  }
  const keyword = searchKeyword.value.toLowerCase()
  return props.sessions.filter(
    (session) =>
      session.title?.toLowerCase().includes(keyword) ||
      session.sessionNo?.toLowerCase().includes(keyword)
  )
})

const formatTime = (time: string) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))

  if (days === 0) {
    return '今天'
  } else if (days === 1) {
    return '昨天'
  } else if (days < 7) {
    return `${days}天前`
  } else {
    return date.toLocaleDateString()
  }
}

const getStatusType = (status: number) => {
  const map: Record<number, string> = {
    0: 'warning',
    1: 'success',
    2: 'info',
  }
  return map[status] || 'info'
}

const getStatusText = (status: number) => {
  const map: Record<number, string> = {
    0: '进行中',
    1: '已结束',
    2: '已取消',
  }
  return map[status] || '未知'
}
</script>

<style scoped>
.session-list-panel {
  width: 360px;
  background: #fff;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.panel-header {
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
}

.panel-header h3 {
  margin: 0 0 16px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.search-input {
  width: 100%;
}

.session-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.session-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 8px;
}

.session-item:hover {
  background: #f5f7fa;
}

.session-item.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.session-item.active .session-title,
.session-item.active .session-meta {
  color: #fff;
}

.session-avatar {
  flex-shrink: 0;
}

.session-avatar .ai-icon {
  color: #764ba2;
}

.session-avatar .doctor-icon {
  color: #409eff;
}

.session-item.active .session-avatar .ai-icon,
.session-item.active .session-avatar .doctor-icon {
  color: #fff;
}

.session-info {
  flex: 1;
  min-width: 0;
}

.session-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.session-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #909399;
}

.session-status {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.session-unread-badge {
  margin-left: 4px;
}

.empty-sessions {
  padding: 40px 20px;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .session-list-panel {
    width: 100%;
    max-height: 300px;
  }
}
</style>

