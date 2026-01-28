<template>
  <div class="consultation-view">
    <!-- 顶部操作栏 -->
    <ConsultationHeader @create="showCreateDialog = true" />

    <!-- 问诊会话列表 -->
    <div class="consultation-container">
      <!-- 左侧会话列表 -->
      <SessionList
        :sessions="sessions"
        :current-session-id="currentSessionId"
        @select="selectSession"
        @create="showCreateDialog = true"
      />

      <!-- 右侧聊天区域 -->
      <ChatPanel
        :session="currentSession"
        :messages="messages"
        :loading-messages="loadingMessages"
        :sending="sending"
        @create="showCreateDialog = true"
        @end="endSession"
        @send="handleSendMessage"
        @image-pick="showImagePicker = true"
      />
    </div>

    <!-- 创建问诊对话框 -->
    <CreateSessionDialog v-model="showCreateDialog" :creating="creating" @submit="createSession" />

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
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import ConsultationHeader from '../../components/patient/ConsultationHeader.vue'
import SessionList from '../../components/patient/SessionList.vue'
import ChatPanel from '../../components/patient/ChatPanel.vue'
import CreateSessionDialog from '../../components/patient/CreateSessionDialog.vue'
import {
  createConsultationSession,
  getConsultationMessages,
  sendMessage,
  endConsultationSession,
  uploadConsultationFile,
  type ConsultationSession,
  type ConsultationMessage,
} from '../../api/patient/consultation'
import { useConsultationMessageStore } from '../../stores/consultationMessage'
import { useChatStore } from '../../stores/chat'
import { useSessionListStore } from '../../stores/sessionList'
import type { WebSocketMessage } from '../../utils/websocket'

const messageStore = useConsultationMessageStore()
const chatStore = useChatStore()
const sessionListStore = useSessionListStore()

// 从 store 读取会话列表
const sessions = computed(() => sessionListStore.sessions as ConsultationSession[])

const currentSessionId = ref<number | null>(null)
const currentSession = ref<ConsultationSession | null>(null)
const messages = ref<ConsultationMessage[]>([])
const sending = ref(false)
const loadingMessages = ref(false)
const showCreateDialog = ref(false)
const creating = ref(false)
const showImagePicker = ref(false)
const imageInput = ref<HTMLInputElement | null>(null)

// WebSocket订阅取消函数
let unsubscribeMessage: (() => void) | null = null
let unsubscribeStatus: (() => void) | null = null
let unsubscribeGlobalMessages: (() => void) | null = null

onMounted(async () => {
  // 初始加载会话列表
  await loadSessions()

  // 订阅全局消息，响应消息变化自动更新列表
  unsubscribeGlobalMessages = messageStore.subscribeGlobal((message: WebSocketMessage) => {
    // 处理新消息，更新会话列表
    sessionListStore.handleNewMessage(message)
  })

  // 检查URL参数
  const urlParams = new URLSearchParams(window.location.search)
  const sessionId = urlParams.get('sessionId')
  if (sessionId) {
    selectSession(Number(sessionId))
  } else if (sessions.value.length > 0 && !currentSessionId.value) {
    // 如果没有选中会话，选中第一个
    const firstSession = sessions.value[0]
    if (firstSession) {
      selectSession(firstSession.id)
    }
  }
})

onUnmounted(() => {
  // 取消订阅
  if (unsubscribeMessage) {
    unsubscribeMessage()
    unsubscribeMessage = null
  }
  if (unsubscribeStatus) {
    unsubscribeStatus()
    unsubscribeStatus = null
  }
  if (unsubscribeGlobalMessages) {
    unsubscribeGlobalMessages()
    unsubscribeGlobalMessages = null
  }
  // 关闭当前会话
  chatStore.closeSession()
  // 注意：WebSocket连接在登录时建立，登出时断开，这里不需要断开
})

// 监听当前会话变化，加载消息并订阅WebSocket
watch(currentSessionId, async (newId) => {
  // 取消之前的订阅
  if (unsubscribeMessage) {
    unsubscribeMessage()
    unsubscribeMessage = null
  }
  if (unsubscribeStatus) {
    unsubscribeStatus()
    unsubscribeStatus = null
  }

  if (!newId) {
    messages.value = []
    chatStore.closeSession()
    return
  }

  // 加载历史消息
  await loadMessages(newId)

  // 通知 store 打开会话（统一处理已读逻辑）
  await chatStore.openSession(newId)

  // 订阅消息更新
  unsubscribeMessage = messageStore.subscribeSession(newId, (message: WebSocketMessage) => {
    // 组件不判断消息归属，只负责展示
    const exists = messages.value.some((m) => m.id === message.id)
    if (!exists) {
      messages.value = [...messages.value, message as ConsultationMessage]
      // 使用nextTick确保DOM更新后再滚动
      nextTick(() => {
        const messageListElement = document.querySelector('.messages-container')
        if (messageListElement) {
          messageListElement.scrollTop = messageListElement.scrollHeight
        }
      })
    }
  })

  // 确保该会话已添加到消息分发中心
  messageStore.addSession(newId)

  // 订阅状态更新（状态更新仍然直接使用WebSocket，因为不是消息）
  const { websocketClient } = await import('../../utils/websocket')
  if (websocketClient.isConnected()) {
    try {
      unsubscribeStatus = websocketClient.subscribeToSessionStatus(newId, (status: number) => {
        if (currentSession.value) {
          currentSession.value.status = status
        }
        // 更新 store 中的会话状态（事件驱动）
        sessionListStore.updateSessionStatus(newId, status)
      })
    } catch (error: unknown) {
      console.error('订阅WebSocket状态失败:', error)
    }
  }
})

// 加载会话列表（从 store 调用）
const loadSessions = async () => {
  try {
    await sessionListStore.loadSessions()
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : '加载问诊列表失败'
    ElMessage.error(message)
  }
}

const selectSession = async (sessionId: number) => {
  currentSessionId.value = sessionId
  const session = sessions.value.find((s) => s.id === sessionId)
  currentSession.value = session || null
}

const loadMessages = async (sessionId: number) => {
  loadingMessages.value = true
  try {
    const response = await getConsultationMessages(sessionId, { current: 1, size: 100 })
    if (response.data) {
      messages.value = response.data.records || []
    }
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : '加载消息失败'
    ElMessage.error(message)
  } finally {
    loadingMessages.value = false
  }
}

const createSession = async (data: { sessionType: number; title: string; doctorId?: number }) => {
  creating.value = true
  try {
    const response = await createConsultationSession({
      sessionType: data.sessionType,
      title: data.title,
      doctorId: data.doctorId,
    })
    if (response.data) {
      ElMessage.success('创建问诊成功')
      showCreateDialog.value = false
      // 重新加载会话列表（新创建的会话需要添加到列表）
      await loadSessions()
      selectSession(response.data.id)
    }
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : '创建问诊失败'
    ElMessage.error(message)
  } finally {
    creating.value = false
  }
}

const handleSendMessage = async (message: string) => {
  if (!currentSessionId.value) {
    return
  }
  sending.value = true
  try {
    const response = await sendMessage({
      sessionId: currentSessionId.value,
      messageType: 1,
      content: message,
    })

    // 乐观更新：立即在本地显示发送的消息，确保实时性
    if (response.data) {
      const exists = messages.value.some((m) => m.id === response.data.id)
      if (!exists) {
        messages.value.push(response.data)
        // 滚动到底部
        nextTick(() => {
          const messageListElement = document.querySelector('.messages-container')
          if (messageListElement) {
            messageListElement.scrollTop = messageListElement.scrollHeight
          }
        })
      }
    }
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : '发送消息失败'
    ElMessage.error(message)
  } finally {
    sending.value = false
  }
}

const endSession = async () => {
  if (!currentSessionId.value) return
  try {
    await ElMessageBox.confirm('确定要结束此问诊吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await endConsultationSession(currentSessionId.value)
    ElMessage.success('问诊已结束')

    // 更新 store 中的会话状态（事件驱动）
    sessionListStore.updateSessionStatus(currentSessionId.value, 1)

    if (currentSession.value) {
      currentSession.value.status = 1
    }
  } catch (error: unknown) {
    if (error !== 'cancel') {
      const message = error instanceof Error ? error.message : '结束问诊失败'
      ElMessage.error(message)
    }
  }
}

const handleImageSelect = async (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (!file || !currentSessionId.value) return

  // 仅允许图片
  if (!file.type.startsWith('image/')) {
    ElMessage.warning('请选择图片文件')
    target.value = ''
    return
  }

  sending.value = true
  try {
    const uploadResp = await uploadConsultationFile(file)
    const url = uploadResp.data?.fileUrl
    if (!url) {
      throw new Error('上传失败：未返回文件URL')
    }

    const response = await sendMessage({
      sessionId: currentSessionId.value,
      messageType: 2,
      content: url,
    })

    // 乐观更新：立即在本地显示发送的消息
    if (response.data) {
      const exists = messages.value.some((m) => m.id === response.data.id)
      if (!exists) {
        messages.value.push(response.data)
        nextTick(() => {
          const messageListElement = document.querySelector('.messages-container')
          if (messageListElement) {
            messageListElement.scrollTop = messageListElement.scrollHeight
          }
        })
      }
    }
  } catch (error: unknown) {
    const msg = error instanceof Error ? error.message : '图片发送失败'
    ElMessage.error(msg)
  } finally {
    sending.value = false
    target.value = ''
  }
}

watch(showImagePicker, (val) => {
  if (val && imageInput.value) {
    imageInput.value.click()
    showImagePicker.value = false
  }
})
</script>

<style scoped>
.consultation-view {
  height: calc(100vh - 120px);
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

.consultation-container {
  flex: 1;
  display: flex;
  gap: 16px;
  overflow: hidden;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .consultation-container {
    flex-direction: column;
  }
}
</style>
