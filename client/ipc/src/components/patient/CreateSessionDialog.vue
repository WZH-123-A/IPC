<template>
  <el-dialog
    v-model="visible"
    title="新建问诊"
    width="500px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form :model="form" label-width="100px">
      <el-form-item label="问诊类型" required>
        <el-radio-group v-model="form.sessionType">
          <el-radio :label="1">
            <div class="consultation-type-option">
              <el-icon><ChatDotRound /></el-icon>
              <div>
                <div class="type-name">AI问诊</div>
                <div class="type-desc">快速获得AI智能建议</div>
              </div>
            </div>
          </el-radio>
          <el-radio :label="2" style="margin-top: 16px">
            <div class="consultation-type-option">
              <el-icon><UserFilled /></el-icon>
              <div>
                <div class="type-name">医生问诊</div>
                <div class="type-desc">与专业医生进行详细咨询</div>
              </div>
            </div>
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="问诊标题" required>
        <el-input
          v-model="form.title"
          placeholder="请输入问诊标题或主诉"
          maxlength="50"
          show-word-limit
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="creating" @click="handleSubmit">创建</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { ChatDotRound, UserFilled } from '@element-plus/icons-vue'

interface Props {
  modelValue: boolean
  creating?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  creating: false,
})

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  submit: [data: { sessionType: number; title: string }]
}>()

const visible = ref(props.modelValue)
const form = ref({
  sessionType: 1,
  title: '',
})

watch(() => props.modelValue, (val) => {
  visible.value = val
})

watch(visible, (val) => {
  emit('update:modelValue', val)
  if (!val) {
    form.value = { sessionType: 1, title: '' }
  }
})

const handleClose = () => {
  visible.value = false
}

const handleSubmit = () => {
  if (!form.value.title.trim()) {
    return
  }
  emit('submit', {
    sessionType: form.value.sessionType,
    title: form.value.title.trim(),
  })
}
</script>

<style scoped>
.consultation-type-option {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px;
}

.consultation-type-option .el-icon {
  font-size: 24px;
  color: #667eea;
}

.type-name {
  font-weight: 600;
  color: #303133;
}

.type-desc {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
</style>

