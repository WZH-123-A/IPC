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
    <CreateSessionDialog
      v-model="showCreateDialog"
      :creating="creating"
      @submit="createSession"
    />

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
import { ref, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import ConsultationHeader from '../../components/patient/ConsultationHeader.vue'
import SessionList from '../../components/patient/SessionList.vue'
import ChatPanel from '../../components/patient/ChatPanel.vue'
import CreateSessionDialog from '../../components/patient/CreateSessionDialog.vue'
import {
  getConsultationSessions,
  createConsultationSession,
  getConsultationMessages,
  sendMessage,
  endConsultationSession,
  type ConsultationSession,
  type ConsultationMessage,
} from '../../api/patient/consultation'

const sessions = ref<ConsultationSession[]>([])
const currentSessionId = ref<number | null>(null)
const currentSession = ref<ConsultationSession | null>(null)
const messages = ref<ConsultationMessage[]>([])
const sending = ref(false)
const loadingMessages = ref(false)
const showCreateDialog = ref(false)
const creating = ref(false)
const showImagePicker = ref(false)
const imageInput = ref<HTMLInputElement | null>(null)

onMounted(async () => {
  await loadSessions()
  // 检查URL参数
  const urlParams = new URLSearchParams(window.location.search)
  const sessionId = urlParams.get('sessionId')
  if (sessionId) {
    selectSession(Number(sessionId))
  }
})

// 监听当前会话变化，加载消息
watch(currentSessionId, async (newId) => {
  if (newId) {
    await loadMessages(newId)
  } else {
    messages.value = []
  }
})

const loadSessions = async () => {
  try {
    const response = await getConsultationSessions({ current: 1, size: 100 })
    if (response.data) {
      sessions.value = response.data.records || []
      // 如果没有选中会话，选中第一个
      if (sessions.value.length > 0 && !currentSessionId.value) {
        const firstSession = sessions.value[0]
        if (firstSession) {
          selectSession(firstSession.id)
        }
      }
    }
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

const createSession = async (data: { sessionType: number; title: string }) => {
  creating.value = true
  try {
    const response = await createConsultationSession({
      sessionType: data.sessionType,
      title: data.title,
    })
    if (response.data) {
      ElMessage.success('创建问诊成功')
      showCreateDialog.value = false
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
    if (response.data) {
      messages.value.push(response.data)
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
    await loadSessions()
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

  // TODO: 实现图片上传和发送
  ElMessage.info('图片上传功能开发中...')
  target.value = ''
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
