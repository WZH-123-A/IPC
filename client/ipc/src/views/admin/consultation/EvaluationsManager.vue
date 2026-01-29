<template>
  <BaseManageView
    ref="baseRef"
    title="问诊评价管理"
    :show-add-button="true"
    :show-selection="false"
    add-permission="consultation:evaluation:create"
    edit-permission="consultation:evaluation:edit"
    delete-permission="consultation:evaluation:delete"
    @search="handleSearch"
    @add="handleAdd"
    @edit="handleEdit"
    @delete="handleDelete"
    @submit="handleSubmit"
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

    <template #dialog-form="{ formData }">
      <el-form :model="formData" label-width="90px">
        <el-form-item label="会话ID">
          <el-input-number
            v-model="formData.sessionId"
            placeholder="会话ID"
            :min="1"
            :disabled="!!formData.id"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="患者ID">
          <el-input-number
            v-model="formData.patientId"
            placeholder="患者ID"
            :min="1"
            :disabled="!!formData.id"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="医生ID">
          <el-input-number
            v-model="formData.doctorId"
            placeholder="选填，AI问诊留空"
            :min="1"
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="评分">
          <el-rate v-model="formData.rating" :max="5" show-text />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input v-model="formData.comment" type="textarea" :rows="3" placeholder="评价内容" />
        </el-form-item>
      </el-form>
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

    <template #actions="{ row }">
      <el-button
        v-permission="'consultation:evaluation:edit'"
        type="primary"
        link
        size="small"
        @click="handleEditClick(row as EvaluationRow)"
      >
        编辑
      </el-button>
      <el-button
        v-permission="'consultation:evaluation:delete'"
        type="danger"
        link
        size="small"
        @click="handleDeleteClick(row as EvaluationRow)"
      >
        删除
      </el-button>
    </template>
  </BaseManageView>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import BaseManageView from '../../../components/admin/BaseManageView.vue'
import {
  getConsultationEvaluationListApi,
  createConsultationEvaluationApi,
  updateConsultationEvaluationApi,
  deleteConsultationEvaluationApi,
  type ConsultationEvaluationListParams,
  type ConsultationEvaluation,
} from '../../../api/admin/consultation'

interface EvaluationRow extends ConsultationEvaluation {
  id: number
}

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

const handleAdd = () => {
  const formData = baseRef.value?.formData as Record<string, unknown>
  if (formData) {
    formData.id = undefined
    formData.sessionId = undefined
    formData.patientId = undefined
    formData.doctorId = undefined
    formData.rating = 3
    formData.comment = ''
  }
}

const handleEdit = () => {
  // 编辑时 formData 已由 BaseManageView 从 row 赋值
}

const handleEditClick = (row: EvaluationRow) => {
  baseRef.value?.handleEdit(row as unknown as Record<string, unknown>)
}

const handleDelete = async (row: Record<string, unknown>) => {
  const id = row.id as number
  if (id == null) return
  try {
    await ElMessageBox.confirm('确定要删除该评价吗？删除后不可恢复。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await deleteConsultationEvaluationApi(id)
    ElMessage.success('删除成功')
    loadEvaluationList()
  } catch (error: unknown) {
    if ((error as string) !== 'cancel') {
      const message = error instanceof Error ? error.message : '删除失败'
      ElMessage.error(message)
    }
  }
}

const handleDeleteClick = (row: EvaluationRow) => {
  handleDelete(row as unknown as Record<string, unknown>)
}

const handleSubmit = async (payload: Record<string, unknown> & { isEdit?: boolean }) => {
  const isEdit = payload.isEdit
  const formData = baseRef.value?.formData as Record<string, unknown>
  if (!formData) return
  try {
    if (isEdit && formData.id != null) {
      await updateConsultationEvaluationApi(Number(formData.id), {
        rating: formData.rating as number,
        comment: formData.comment as string,
      })
      ElMessage.success('更新成功')
    } else {
      await createConsultationEvaluationApi({
        sessionId: Number(formData.sessionId),
        patientId: Number(formData.patientId),
        doctorId: formData.doctorId != null ? Number(formData.doctorId) : undefined,
        rating: Number(formData.rating),
        comment: (formData.comment as string) || undefined,
      })
      ElMessage.success('新增成功')
    }
    baseRef.value!.dialogVisible = false
    loadEvaluationList()
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : '操作失败'
    ElMessage.error(message)
  }
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
