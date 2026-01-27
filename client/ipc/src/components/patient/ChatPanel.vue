<template>
  <div class="chat-panel">
    <div v-if="!session" class="empty-chat">
      <el-empty description="请选择一个问诊会话或创建新问诊" :image-size="200">
        <el-button type="primary" size="large" @click="$emit('create')">
          <el-icon><Plus /></el-icon>
          <span>新建问诊</span>
        </el-button>
      </el-empty>
    </div>
    <div v-else class="chat-content">
      <!-- 聊天头部 -->
      <div class="chat-header">
        <div class="chat-title">
          <el-icon :size="20" :class="session.sessionType === 1 ? 'ai-icon' : 'doctor-icon'">
            <component :is="session.sessionType === 1 ? 'ChatDotRound' : 'UserFilled'" />
          </el-icon>
          <span>{{ session.title || '未命名问诊' }}</span>
        </div>
        <div class="chat-actions">
          <el-button
            v-if="session.status === 0"
            type="danger"
            size="small"
            @click="$emit('end')"
          >
            结束问诊
          </el-button>
        </div>
      </div>

      <!-- 消息列表 -->
      <MessageList :messages="messages" :loading="loadingMessages" ref="messageListRef" />

      <!-- 输入区域 -->
      <ChatInput
        v-if="session.status === 0"
        :sending="sending"
        @send="handleSend"
        @image-pick="$emit('image-pick')"
      />
      <div v-else class="chat-ended">
        <el-alert type="info" :closable="false">此问诊已结束</el-alert>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { Plus, ChatDotRound, UserFilled } from '@element-plus/icons-vue'
import MessageList from './MessageList.vue'
import ChatInput from './ChatInput.vue'
import type { ConsultationSession, ConsultationMessage } from '../../api/patient/consultation'

interface Props {
  session: ConsultationSession | null
  messages: ConsultationMessage[]
  loadingMessages?: boolean
  sending?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  loadingMessages: false,
  sending: false,
})

const emit = defineEmits<{
  create: []
  end: []
  send: [message: string]
  'image-pick': []
}>()

const messageListRef = ref<InstanceType<typeof MessageList> | null>(null)

const handleSend = (message: string) => {
  emit('send', message)
  nextTick(() => {
    messageListRef.value?.scrollToBottom()
  })
}
</script>

<style scoped>
.chat-panel {
  flex: 1;
  background: #fff;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.empty-chat {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chat-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #ebeef5;
}

.chat-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.chat-ended {
  padding: 16px 20px;
  border-top: 1px solid #ebeef5;
}
</style>

