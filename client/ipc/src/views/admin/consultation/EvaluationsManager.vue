<template>
  <BaseManageView
    ref="baseRef"
    title="问诊评价管理"
    :show-add-button="false"
    :show-selection="false"
    @search="handleSearch"
  >
    <template #search-form="{ searchForm }">
      <el-form-item label="会话ID">
        <el-input-number
          v-model="searchForm.sessionId"
          placeholder="会话ID"
          :min="1"
          clearable
        />
      </el-form-item>
      <el-form-item label="患者ID">
        <el-input-number
          v-model="searchForm.patientId"
          placeholder="患者ID"
          :min="1"
          clearable
        />
      </el-form-item>
      <el-form-item label="医生ID">
        <el-input-number
          v-model="searchForm.doctorId"
          placeholder="医生ID"
          :min="1"
          clearable
        />
      </el-form-item>
      <el-form-item label="评分">
        <el-select v-model="searchForm.rating" placeholder="请选择评分" clearable>
          <el-option label="1星" :value="1" />
          <el-option label="2星" :value="2" />
          <el-option label="3星" :value="3" />
          <el-option label="4星" :value="4" />
          <el-option label="5星" :value="5" />
        </el-select>
      </el-form-item>
    </template>

    <template #table-columns>
      <el-table-column prop="id" label="评价ID" width="90" />
      <el-table-column prop="sessionId" label="会话ID" width="90" />
      <el-table-column prop="sessionNo" label="会话编号" width="160" show-overflow-tooltip />
      <el-table-column prop="patientName" label="患者姓名" width="110">
        <template #default="{ row }">
          {{ row.patientName || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="doctorName" label="医生" width="110">
        <template #default="{ row }">
          {{ row.doctorName || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="rating" label="评分" width="90">
        <template #default="{ row }">
          <el-rate :model-value="row.rating" disabled show-score />
        </template>
      </el-table-column>
      <el-table-column prop="comment" label="评价内容" min-width="200" show-overflow-tooltip />
      <el-table-column prop="createTime" label="创建时间" width="180">
        <template #default="{ row }">
          {{ formatTime(row.createTime) }}
        </template>
      </el-table-column>
    </template>

    <template #actions>
      <span class="no-actions">仅查看</span>
    </template>
  </BaseManageView>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import BaseManageView from '../../../components/admin/BaseManageView.vue'
import {
  getConsultationEvaluationListApi,
  type ConsultationEvaluationListParams,
} from '../../../api/admin/consultation'

const baseRef = ref<InstanceType<typeof BaseManageView>>()

const loadEvaluationList = async () => {
  if (!baseRef.value) return

  baseRef.value.setLoading(true)
  try {
    const searchForm = baseRef.value.searchForm as ConsultationEvaluationListParams
    const pagination = baseRef.value.pagination

    const params: ConsultationEvaluationListParams = {
      current: pagination.current,
      size: pagination.size,
      sessionId: searchForm.sessionId,
      patientId: searchForm.patientId,
      doctorId: searchForm.doctorId,
      rating: searchForm.rating,
    }
    const result = await getConsultationEvaluationListApi(params)
    baseRef.value.setDataList((result.records || []) as unknown as Record<string, unknown>[])
    baseRef.value.setPagination({
      current: result.current || 1,
      size: result.size || 10,
      total: result.total || 0,
    })
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : '加载评价列表失败'
    ElMessage.error(message)
  } finally {
    baseRef.value.setLoading(false)
  }
}

const handleSearch = () => {
  loadEvaluationList()
}

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

onMounted(() => {
  loadEvaluationList()
})
</script>

<style scoped>
.no-actions {
  color: var(--el-text-color-secondary);
  font-size: 12px;
}
</style>
