<template>
  <el-dialog
    v-model="visible"
    title="新建问诊"
    width="600px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form :model="form" label-width="100px">
      <el-form-item label="问诊类型" required>
        <el-radio-group v-model="form.sessionType" @change="handleTypeChange">
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
      <el-form-item
        v-if="form.sessionType === 2"
        label="选择医生"
        required
      >
        <el-select
          v-model="form.doctorId"
          placeholder="请选择医生"
          filterable
          style="width: 100%"
          :loading="loadingDoctors"
        >
          <el-option
            v-for="doctor in doctors"
            :key="doctor.id"
            :label="getDoctorLabel(doctor)"
            :value="doctor.id"
          >
            <div class="doctor-option">
              <el-avatar :src="doctor.avatar" :size="32">
                {{ doctor.realName?.charAt(0) || '医' }}
              </el-avatar>
              <div class="doctor-info">
                <div class="doctor-name">{{ doctor.realName }}</div>
                <div class="doctor-desc">
                  <span v-if="doctor.hospital">{{ doctor.hospital }}</span>
                  <span v-if="doctor.department">{{ doctor.department }}</span>
                  <span v-if="doctor.title">{{ doctor.title }}</span>
                </div>
                <div v-if="doctor.specialty" class="doctor-specialty">
                  擅长：{{ doctor.specialty }}
                </div>
              </div>
            </div>
          </el-option>
        </el-select>
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
import { ref, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { ChatDotRound, UserFilled } from '@element-plus/icons-vue'
import { getAvailableDoctors, type DoctorSimple } from '../../api/patient/consultation'

interface Props {
  modelValue: boolean
  creating?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  creating: false,
})

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  submit: [data: { sessionType: number; title: string; doctorId?: number }]
}>()

const visible = ref(props.modelValue)
const form = ref({
  sessionType: 1,
  title: '',
  doctorId: undefined as number | undefined,
})
const doctors = ref<DoctorSimple[]>([])
const loadingDoctors = ref(false)

watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val && form.value.sessionType === 2 && doctors.value.length === 0) {
    loadDoctors()
  }
})

watch(visible, (val) => {
  emit('update:modelValue', val)
  if (!val) {
    form.value = { sessionType: 1, title: '', doctorId: undefined }
  }
})

const loadDoctors = async () => {
  if (loadingDoctors.value) return
  loadingDoctors.value = true
  try {
    const response = await getAvailableDoctors()
    if (response.code === 200 && response.data) {
      doctors.value = response.data
    } else {
      ElMessage.error(response.message || '获取医生列表失败')
    }
  } catch (error) {
    console.error('获取医生列表失败:', error)
    ElMessage.error('获取医生列表失败')
  } finally {
    loadingDoctors.value = false
  }
}

const handleTypeChange = (type: number) => {
  if (type === 2 && doctors.value.length === 0) {
    loadDoctors()
  } else if (type === 1) {
    form.value.doctorId = undefined
  }
}

const getDoctorLabel = (doctor: DoctorSimple): string => {
  const parts: string[] = []
  if (doctor.realName) parts.push(doctor.realName)
  if (doctor.title) parts.push(doctor.title)
  if (doctor.department) parts.push(doctor.department)
  if (doctor.hospital) parts.push(doctor.hospital)
  return parts.join(' - ')
}

const handleClose = () => {
  visible.value = false
}

const handleSubmit = () => {
  if (!form.value.title.trim()) {
    ElMessage.warning('请输入问诊标题')
    return
  }
  if (form.value.sessionType === 2 && !form.value.doctorId) {
    ElMessage.warning('请选择医生')
    return
  }
  emit('submit', {
    sessionType: form.value.sessionType,
    title: form.value.title.trim(),
    doctorId: form.value.sessionType === 2 ? form.value.doctorId : undefined,
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

.doctor-option {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 4px 0;
}

.doctor-info {
  flex: 1;
  min-width: 0;
}

.doctor-name {
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.doctor-desc {
  font-size: 12px;
  color: #909399;
  margin-bottom: 2px;
}

.doctor-desc span {
  margin-right: 8px;
}

.doctor-specialty {
  font-size: 12px;
  color: #606266;
  margin-top: 2px;
}
</style>

