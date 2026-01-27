<template>
  <div class="messages-container" ref="messagesContainer">
    <div
      v-for="message in messages"
      :key="message.id"
      class="message-item"
      :class="{
        'message-patient': message.senderType === 1,
        'message-doctor': message.senderType === 2,
        'message-ai': message.senderType === 3,
      }"
    >
      <div class="message-avatar">
        <el-avatar :size="36">
          <el-icon v-if="message.senderType === 3">
            <ChatDotRound />
          </el-icon>
          <el-icon v-else>
            <UserFilled />
          </el-icon>
        </el-avatar>
      </div>
      <div class="message-content">
        <div class="message-header">
          <span class="message-sender">
            {{
              message.senderType === 1
                ? '我'
                : message.senderType === 2
                ? '医生'
                : 'AI助手'
            }}
          </span>
          <span class="message-time">{{ formatMessageTime(message.createTime) }}</span>
        </div>
        <div class="message-body">
          <div v-if="message.messageType === 1" class="message-text">{{ message.content }}</div>
          <div v-else-if="message.messageType === 2" class="message-image">
            <el-image :src="message.content" fit="cover" :preview-src-list="[message.content]" />
          </div>
        </div>
      </div>
    </div>
    <div v-if="loading" class="loading-messages">
      <el-icon class="is-loading"><Loading /></el-icon>
      <span>加载中...</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, nextTick, onMounted } from 'vue'
import { ChatDotRound, UserFilled, Loading } from '@element-plus/icons-vue'
import type { ConsultationMessage } from '../../api/patient/consultation'

interface Props {
  messages: ConsultationMessage[]
  loading?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
})

const messagesContainer = ref<HTMLElement | null>(null)

const formatMessageTime = (time: string) => {
  if (!time) return ''
  const date = new Date(time)
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  return `${hours}:${minutes}`
}

const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

watch(
  () => props.messages.length,
  () => {
    nextTick(() => {
      scrollToBottom()
    })
  }
)

onMounted(() => {
  scrollToBottom()
})

defineExpose({
  scrollToBottom,
})
</script>

<style scoped>
.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f5f7fa;
}

.message-item {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.message-item.message-patient {
  flex-direction: row-reverse;
}

.message-content {
  max-width: 70%;
  display: flex;
  flex-direction: column;
}

.message-item.message-patient .message-content {
  align-items: flex-end;
}

.message-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
  font-size: 12px;
  color: #909399;
}

.message-item.message-patient .message-header {
  flex-direction: row-reverse;
}

.message-sender {
  font-weight: 500;
}

.message-body {
  background: #fff;
  padding: 12px 16px;
  border-radius: 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.message-item.message-patient .message-body {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.message-item.message-ai .message-body {
  background: #f0f9ff;
  border-left: 3px solid #764ba2;
}

.message-text {
  line-height: 1.6;
  word-wrap: break-word;
}

.message-image {
  max-width: 300px;
}

.loading-messages {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 20px;
  color: #909399;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .message-content {
    max-width: 85%;
  }
}
</style>

