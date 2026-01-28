<template>
  <BaseManageView
    ref="baseRef"
    title="问诊会话管理"
    :show-add-button="false"
    :show-selection="false"
    @search="handleSearch"
  >
    <!-- 自定义搜索表单 -->
    <template #search-form="{ searchForm }">
      <el-form-item label="会话编号">
        <el-input v-model="searchForm.sessionNo" placeholder="请输入会话编号" clearable />
      </el-form-item>
      <el-form-item label="患者ID">
        <el-input-number
          v-model="searchForm.patientId"
          placeholder="请输入患者ID"
          :min="1"
          clearable
        />
      </el-form-item>
      <el-form-item label="医生ID">
        <el-input-number
          v-model="searchForm.doctorId"
          placeholder="请输入医生ID"
          :min="1"
          clearable
        />
      </el-form-item>
      <el-form-item label="会话类型">
        <el-select v-model="searchForm.sessionType" placeholder="请选择会话类型" clearable>
          <el-option label="AI问诊" :value="1" />
          <el-option label="医生问诊" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
          <el-option label="进行中" :value="0" />
          <el-option label="已结束" :value="1" />
          <el-option label="已取消" :value="2" />
        </el-select>
      </el-form-item>
    </template>

    <!-- 自定义表格列 -->
    <template #table-columns>
      <el-table-column prop="sessionNo" label="会话编号" width="180" />
      <el-table-column prop="patientName" label="患者姓名" width="120" />
      <el-table-column prop="doctorName" label="医生姓名" width="120">
        <template #default="{ row }">
          <span>{{ row.doctorName || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="sessionType" label="会话类型" width="100">
        <template #default="{ row }">
          <el-tag :type="row.sessionType === 1 ? 'info' : 'primary'">
            {{ row.sessionType === 1 ? 'AI问诊' : '医生问诊' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="问诊标题" min-width="200" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="startTime" label="开始时间" width="180">
        <template #default="{ row }">
          <span>{{ formatTime(row.startTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="endTime" label="结束时间" width="180">
        <template #default="{ row }">
          <span>{{ formatTime(row.endTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180">
        <template #default="{ row }">
          <span>{{ formatTime(row.createTime) }}</span>
        </template>
      </el-table-column>
    </template>

    <!-- 自定义操作按钮 -->
    <template #actions="{ row }">
      <el-button
        v-permission="'consultation:session:detail'"
        type="primary"
        link
        size="small"
        @click="handleView(row as ConsultationSession)"
      >
        查看详情
      </el-button>
    </template>
  </BaseManageView>

  <!-- 详情对话框 -->
  <el-dialog
    v-model="detailDialogVisible"
    title="问诊会话详情"
    width="800px"
    @close="handleDetailDialogClose"
  >
    <el-descriptions :column="2" border v-if="currentSession">
      <el-descriptions-item label="会话ID">{{ currentSession.id }}</el-descriptions-item>
      <el-descriptions-item label="会话编号">{{ currentSession.sessionNo }}</el-descriptions-item>
      <el-descriptions-item label="患者ID">{{ currentSession.patientId }}</el-descriptions-item>
      <el-descriptions-item label="患者姓名">{{
        currentSession.patientName || '-'
      }}</el-descriptions-item>
      <el-descriptions-item label="医生ID">{{
        currentSession.doctorId || '-'
      }}</el-descriptions-item>
      <el-descriptions-item label="医生姓名">{{
        currentSession.doctorName || '-'
      }}</el-descriptions-item>
      <el-descriptions-item label="会话类型">
        <el-tag :type="currentSession.sessionType === 1 ? 'info' : 'primary'">
          {{ currentSession.sessionType === 1 ? 'AI问诊' : '医生问诊' }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="getStatusType(currentSession.status)">
          {{ getStatusText(currentSession.status) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="问诊标题" :span="2">{{
        currentSession.title || '-'
      }}</el-descriptions-item>
      <el-descriptions-item label="开始时间">{{
        formatTime(currentSession.startTime)
      }}</el-descriptions-item>
      <el-descriptions-item label="结束时间">{{
        formatTime(currentSession.endTime)
      }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{
        formatTime(currentSession.createTime)
      }}</el-descriptions-item>
      <el-descriptions-item label="更新时间">{{
        formatTime(currentSession.updateTime)
      }}</el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <el-button @click="detailDialogVisible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import BaseManageView from '../../../components/admin/BaseManageView.vue'
import {
  getConsultationSessionListApi,
  getConsultationSessionByIdApi,
  type ConsultationSession,
  type ConsultationSessionListParams,
} from '../../../api/admin/consultation'

const baseRef = ref<InstanceType<typeof BaseManageView>>()
const detailDialogVisible = ref(false)
const currentSession = ref<ConsultationSession | null>(null)

// 加载会话列表
const loadSessionList = async () => {
  if (!baseRef.value) return

  baseRef.value.setLoading(true)
  try {
    const searchForm = baseRef.value.searchForm as ConsultationSessionListParams
    const pagination = baseRef.value.pagination

    const params: ConsultationSessionListParams = {
      current: pagination.current,
      size: pagination.size,
      ...searchForm,
    }
    const result = await getConsultationSessionListApi(params)
    baseRef.value.setDataList(result.records as unknown as Record<string, unknown>[])
    baseRef.value.setPagination({
      current: result.current,
      size: result.size,
      total: result.total,
    })
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : '加载会话列表失败'
    ElMessage.error(message)
  } finally {
    baseRef.value.setLoading(false)
  }
}

// 搜索
const handleSearch = () => {
  loadSessionList()
}

// 查看详情
const handleView = async (row: ConsultationSession) => {
  try {
    const session = await getConsultationSessionByIdApi(row.id)
    currentSession.value = session
    detailDialogVisible.value = true
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : '加载会话详情失败'
    ElMessage.error(message)
  }
}

// 关闭详情对话框
const handleDetailDialogClose = () => {
  currentSession.value = null
}

// 格式化时间
const formatTime = (time?: string) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
  })
}

// 获取状态文本
const getStatusText = (status: number) => {
  const statusMap: Record<number, string> = {
    0: '进行中',
    1: '已结束',
    2: '已取消',
  }
  return statusMap[status] || '未知'
}

// 获取状态类型
const getStatusType = (status: number): 'success' | 'warning' | 'danger' | 'info' => {
  const typeMap: Record<number, 'success' | 'warning' | 'danger' | 'info'> = {
    0: 'warning',
    1: 'success',
    2: 'danger',
  }
  return typeMap[status] || 'info'
}

onMounted(() => {
  loadSessionList()
})
</script>

<style scoped>
/* 可以添加自定义样式 */
</style>
