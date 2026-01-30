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
          <!-- AI 文本消息按 Markdown 渲染（标题、表格、列表等） -->
          <div
            v-if="message.messageType === 1 && message.senderType === 3"
            class="message-text message-markdown"
            v-html="renderMarkdown(message.content)"
          />
          <div v-else-if="message.messageType === 1" class="message-text">{{ message.content }}</div>
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
import { renderMarkdown } from '../../utils/markdown'

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

// 监听消息数组变化，确保新消息自动滚动
watch(
  () => props.messages,
  (newMessages, oldMessages) => {
    // 如果消息数量增加，说明有新消息
    if (newMessages.length > (oldMessages?.length || 0)) {
      nextTick(() => {
        scrollToBottom()
      })
    }
  },
  { deep: true }
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

/* AI Markdown 渲染样式：标题、表格、列表等 */
.message-markdown :deep(h1),
.message-markdown :deep(h2),
.message-markdown :deep(h3),
.message-markdown :deep(h4),
.message-markdown :deep(h5),
.message-markdown :deep(h6) {
  margin: 0.75em 0 0.35em;
  font-weight: 600;
  line-height: 1.3;
}
.message-markdown :deep(h1) { font-size: 1.25em; }
.message-markdown :deep(h2) { font-size: 1.15em; }
.message-markdown :deep(h3) { font-size: 1.05em; }
.message-markdown :deep(p) { margin: 0.5em 0; }
.message-markdown :deep(ul),
.message-markdown :deep(ol) {
  margin: 0.5em 0;
  padding-left: 1.5em;
}
.message-markdown :deep(li) { margin: 0.25em 0; }
.message-markdown :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: 0.75em 0;
  font-size: 0.9em;
}
.message-markdown :deep(th),
.message-markdown :deep(td) {
  border: 1px solid #e4e7ed;
  padding: 6px 10px;
  text-align: left;
}
.message-markdown :deep(th) {
  background: #f5f7fa;
  font-weight: 600;
}
.message-markdown :deep(blockquote) {
  margin: 0.5em 0;
  padding-left: 1em;
  border-left: 3px solid #c0c4cc;
  color: #606266;
}
.message-markdown :deep(hr) { margin: 1em 0; border: none; border-top: 1px solid #e4e7ed; }
.message-markdown :deep(strong) { font-weight: 600; }
.message-markdown :deep(code) {
  background: #f5f7fa;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 0.9em;
}
.message-markdown :deep(pre) {
  background: #f5f7fa;
  padding: 10px;
  border-radius: 6px;
  overflow-x: auto;
  margin: 0.5em 0;
}
.message-markdown :deep(pre code) { background: none; padding: 0; }

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

