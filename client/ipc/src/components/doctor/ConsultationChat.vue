<template>
  <div class="consultation-chat">
    <!-- 聊天头部 -->
    <div class="chat-header">
      <div class="header-info">
        <el-avatar :src="session?.patientAvatar" :size="40">
          <el-icon><User /></el-icon>
        </el-avatar>
        <div class="info-text">
          <div class="patient-name">{{ session?.patientName || '患者' }}</div>
          <div class="session-title">{{ session?.title || '问诊会话' }}</div>
        </div>
      </div>
      <div class="header-actions">
        <el-tag :type="session?.status === 0 ? 'success' : 'info'">
          {{ session?.status === 0 ? '进行中' : session?.status === 1 ? '已结束' : '已取消' }}
        </el-tag>
        <el-button
          v-if="session?.status === 0"
          type="danger"
          size="small"
          @click="handleEndSession"
        >
          结束问诊
        </el-button>
      </div>
    </div>

    <!-- 消息列表 -->
    <div ref="messagesContainer" class="messages-container">
      <div v-if="loadingMessages" class="loading-wrapper">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span>加载消息中...</span>
      </div>
      <div v-else-if="messages.length === 0" class="empty-messages">
        <el-empty description="暂无消息" :image-size="100" />
      </div>
      <div v-else class="messages-list">
        <div
          v-for="message in messages"
          :key="message.id"
          :class="['message-item', message.senderType === 2 ? 'doctor-message' : 'patient-message']"
        >
          <el-avatar
            :src="message.senderType === 2 ? doctorAvatar : session?.patientAvatar"
            :size="32"
            class="message-avatar"
          >
            <el-icon><User /></el-icon>
          </el-avatar>
          <div class="message-content">
            <div class="message-header">
              <span class="sender-name">
                {{ message.senderType === 2 ? '我' : session?.patientName || '患者' }}
              </span>
              <span class="message-time">{{ formatTime(message.createTime) }}</span>
            </div>
            <div class="message-bubble">
              <div v-if="message.messageType === 1" class="text-message">
                {{ message.content }}
              </div>
              <div v-else-if="message.messageType === 2" class="image-message">
                <el-image
                  :src="message.content"
                  :preview-src-list="[message.content]"
                  fit="cover"
                />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 输入区域 -->
    <div v-if="session?.status === 0" class="chat-input-area">
      <div class="input-toolbar">
        <el-button text @click="showImagePicker = true">
          <el-icon><Picture /></el-icon>
        </el-button>
      </div>
      <div class="input-wrapper">
        <el-input
          v-model="inputMessage"
          type="textarea"
          :rows="3"
          placeholder="请输入回复内容..."
          :disabled="sending"
          @keydown.ctrl.enter="handleSend"
        />
        <div class="input-footer">
          <span class="hint">Ctrl + Enter 发送</span>
          <el-button type="primary" :loading="sending" @click="handleSend"> 发送 </el-button>
        </div>
      </div>
    </div>
    <div v-else class="chat-input-disabled">
      <el-alert type="info" :closable="false"> 问诊已结束，无法继续发送消息 </el-alert>
    </div>

    <!-- 图片选择器 -->
    <input
      ref="imageInput"
      type="file"
      accept="image/*"
      style="display: none"
      @change="handleImageSelect"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, watch, nextTick, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Loading, Picture } from '@element-plus/icons-vue'
import {
  getConsultationMessages,
  sendDoctorMessage,
  uploadConsultationFile,
  endConsultation,
  type ConsultationMessage,
} from '../../api/doctor'
import { useConsultationMessageStore } from '../../stores/consultationMessage'
import { useChatStore } from '../../stores/chat'
import type { WebSocketMessage } from '../../utils/websocket'

interface Props {
  session: {
    id: number
    patientId: number
    patientName?: string
    patientAvatar?: string
    title?: string
    status: number
  } | null
  doctorAvatar?: string
}

const props = defineProps<Props>()

const emit = defineEmits<{
  messageSent: []
  sessionEnded: []
  messagesRead: []
}>()

const messages = ref<ConsultationMessage[]>([])
const inputMessage = ref('')
const sending = ref(false)
const loadingMessages = ref(false)
const showImagePicker = ref(false)
const imageInput = ref<HTMLInputElement | null>(null)
const messagesContainer = ref<HTMLElement | null>(null)
const messageStore = useConsultationMessageStore()
const chatStore = useChatStore()

let unsubscribeMessage: (() => void) | null = null

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

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

// 加载消息
const loadMessages = async () => {
  if (!props.session) return

  loadingMessages.value = true
  try {
    const response = await getConsultationMessages(props.session.id, { current: 1, size: 100 })
    messages.value = response.records || []
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : '加载消息失败'
    ElMessage.error(message)
  } finally {
    loadingMessages.value = false
    scrollToBottom()
  }
}

// 发送消息
const handleSend = async () => {
  if (!props.session || !inputMessage.value.trim() || sending.value) return

  const content = inputMessage.value.trim()
  inputMessage.value = ''
  sending.value = true

  try {
    const response = await sendDoctorMessage({
      sessionId: props.session.id,
      messageType: 1,
      content,
    })

    // 乐观更新：立即在本地显示发送的消息，确保实时性
    if (response) {
      const exists = messages.value.some((m) => m.id === response.id)
      if (!exists) {
        messages.value.push(response)
        scrollToBottom()
      }
    }

    // 通知父组件消息已发送（用于刷新会话列表等）
    emit('messageSent')
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : '发送消息失败'
    ElMessage.error(message)
    inputMessage.value = content // 恢复输入内容
  } finally {
    sending.value = false
  }
}

// 结束问诊
const handleEndSession = async () => {
  if (!props.session) return

  try {
    await ElMessageBox.confirm('确定要结束此次问诊吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    await endConsultation(props.session.id)
    ElMessage.success('问诊已结束')
    emit('sessionEnded')
  } catch (error: unknown) {
    if (error !== 'cancel') {
      const message = error instanceof Error ? error.message : '结束问诊失败'
      ElMessage.error(message)
    }
  }
}

// 选择图片
const handleImageSelect = async (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (!file || !props.session) return

  if (!file.type.startsWith('image/')) {
    ElMessage.warning('请选择图片文件')
    target.value = ''
    return
  }

  sending.value = true
  try {
    const uploaded = await uploadConsultationFile(file)
    if (!uploaded?.fileUrl) {
      throw new Error('上传失败：未返回文件URL')
    }

    const response = await sendDoctorMessage({
      sessionId: props.session.id,
      messageType: 2,
      content: uploaded.fileUrl,
    })

    if (response) {
      const exists = messages.value.some((m) => m.id === response.id)
      if (!exists) {
        messages.value.push(response)
        scrollToBottom()
      }
    }

    emit('messageSent')
  } catch (error: unknown) {
    const msg = error instanceof Error ? error.message : '图片发送失败'
    ElMessage.error(msg)
  } finally {
    sending.value = false
    target.value = ''
  }
}

// 监听会话变化
watch(
  () => props.session?.id,
  async (newId) => {
    // 取消之前的订阅
    if (unsubscribeMessage) {
      unsubscribeMessage()
      unsubscribeMessage = null
    }

    if (!newId) {
      messages.value = []
      chatStore.closeSession()
      return
    }

    // 加载历史消息
    await loadMessages()

    // 通知 store 打开会话（统一处理已读逻辑）
    await chatStore.openSession(newId)

    // 订阅消息更新
    unsubscribeMessage = messageStore.subscribeSession(newId, (message: WebSocketMessage) => {
      // 组件不判断消息归属，只负责展示
      const exists = messages.value.some((m) => m.id === message.id)
      if (!exists) {
        messages.value = [...messages.value, message as ConsultationMessage]
        scrollToBottom()
      }
    })

    // 确保该会话已添加到消息分发中心
    messageStore.addSession(newId)
  },
  { immediate: true },
)

// 监听消息变化，自动滚动
watch(
  () => messages.value,
  (newMessages, oldMessages) => {
    // 如果消息数量增加，说明有新消息，自动滚动
    if (newMessages.length > (oldMessages?.length || 0)) {
      scrollToBottom()
    }
  },
  { deep: true },
)

// 监听图片选择器
watch(showImagePicker, (val) => {
  if (val && imageInput.value) {
    imageInput.value.click()
    showImagePicker.value = false
  }
})

onMounted(() => {
  scrollToBottom()
})

onUnmounted(() => {
  if (unsubscribeMessage) {
    unsubscribeMessage()
    unsubscribeMessage = null
  }
})
</script>

<style scoped>
.consultation-chat {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #f5f7fa;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.header-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.info-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.patient-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.session-title {
  font-size: 12px;
  color: #909399;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f5f7fa;
}

.loading-wrapper,
.empty-messages {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #909399;
}

.messages-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message-item {
  display: flex;
  gap: 12px;
  animation: fadeIn 0.3s ease-in;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.doctor-message {
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
}

.message-content {
  flex: 1;
  max-width: 70%;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.doctor-message .message-content {
  align-items: flex-end;
}

.message-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #909399;
}

.doctor-message .message-header {
  flex-direction: row-reverse;
}

.sender-name {
  font-weight: 500;
}

.message-time {
  font-size: 11px;
}

.message-bubble {
  padding: 10px 14px;
  border-radius: 8px;
  word-wrap: break-word;
  word-break: break-all;
}

.patient-message .message-bubble {
  background: #fff;
  border: 1px solid #e4e7ed;
}

.doctor-message .message-bubble {
  background: #409eff;
  color: #fff;
}

.text-message {
  line-height: 1.5;
}

.image-message :deep(.el-image) {
  max-width: 200px;
  border-radius: 4px;
}

.chat-input-area {
  padding: 16px 20px;
  background: #fff;
  border-top: 1px solid #e4e7ed;
}

.input-toolbar {
  margin-bottom: 8px;
}

.input-wrapper {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.input-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.hint {
  font-size: 12px;
  color: #909399;
}

.chat-input-disabled {
  padding: 16px 20px;
  background: #fff;
  border-top: 1px solid #e4e7ed;
}
</style>
