<template>
  <div class="chat-input">
    <div class="input-toolbar">
      <el-button text @click="$emit('image-pick')">
        <el-icon><Picture /></el-icon>
      </el-button>
    </div>
    <div class="input-area">
      <el-input
        v-model="message"
        type="textarea"
        :rows="3"
        placeholder="输入您的问题..."
        @keydown.enter.exact.prevent="handleSend"
        @keydown.enter.shift.exact="message += '\n'"
      />
      <el-button type="primary" :loading="sending" @click="handleSend">
        <el-icon><Promotion /></el-icon>
        <span>发送</span>
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { Picture, Promotion } from '@element-plus/icons-vue'

interface Props {
  sending?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  sending: false,
})

const emit = defineEmits<{
  send: [message: string]
  'image-pick': []
}>()

const message = ref('')

const handleSend = () => {
  if (!message.value.trim()) {
    return
  }
  emit('send', message.value.trim())
  message.value = ''
}

watch(() => props.sending, (newVal) => {
  if (!newVal) {
    // 发送完成后可以做一些处理
  }
})

defineExpose({
  clear: () => {
    message.value = ''
  },
})
</script>

<style scoped>
.chat-input {
  border-top: 1px solid #ebeef5;
  padding: 16px 20px;
  background: #fff;
}

.input-toolbar {
  margin-bottom: 8px;
}

.input-area {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.input-area .el-textarea {
  flex: 1;
}
</style>

