<template>
  <div class="patient-info-manager">
    <BaseManageView
      ref="baseRef"
      title="患者信息管理"
      :show-add-button="true"
      :show-selection="false"
      add-permission="user-info:patient:add"
      edit-permission="user-info:patient:edit"
      delete-permission="user-info:patient:delete"
      @search="handleSearch"
      @add="handleAdd"
      @submit="handleSubmit"
    >
      <template #search-form="{ searchForm }">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="用户名" clearable style="width: 140px" />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="searchForm.realName" placeholder="真实姓名" clearable style="width: 140px" />
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number
            v-model="searchForm.age"
            :min="1"
            :max="150"
            placeholder="年龄"
            controls-position="right"
            style="width: 120px"
          />
        </el-form-item>
      </template>

      <template #table-columns>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="username" label="用户名" width="110" show-overflow-tooltip />
        <el-table-column prop="realName" label="真实姓名" width="100" show-overflow-tooltip />
        <el-table-column prop="phone" label="手机号" width="120" show-overflow-tooltip />
        <el-table-column prop="age" label="年龄" width="80" align="center" />
        <el-table-column prop="address" label="住址" min-width="160" show-overflow-tooltip />
        <el-table-column prop="medicalHistory" label="既往病史" min-width="120" show-overflow-tooltip />
        <el-table-column prop="allergyHistory" label="过敏史" min-width="120" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="170" show-overflow-tooltip />
      </template>

      <template #actions="{ row }">
        <el-button type="primary" link size="small" @click="openDetail(row as AdminPatientInfoItem)">查看</el-button>
        <el-button
          v-permission="'user-info:patient:edit'"
          type="primary"
          link
          size="small"
          @click="handleEdit(row as AdminPatientInfoItem)"
        >
          编辑
        </el-button>
        <el-button
          v-permission="'user-info:patient:delete'"
          type="danger"
          link
          size="small"
          @click="handleDelete(row as AdminPatientInfoItem)"
        >
          删除
        </el-button>
      </template>

      <template #dialog-form="{ formData }">
        <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
          <el-form-item label="用户" prop="userId">
            <el-select
              v-model="formData.userId"
              placeholder="请选择用户（已具患者角色且未填写信息）"
              filterable
              style="width: 100%"
              :disabled="!!formData.id"
            >
              <el-option
                v-for="u in patientUserOptions"
                :key="u.id"
                :label="`${u.username ?? ''} ${u.realName ? `(${u.realName})` : ''}`.trim() || String(u.id)"
                :value="u.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="年龄" prop="age">
            <el-input-number
              v-model="formData.age"
              :min="1"
              :max="150"
              placeholder="年龄"
              controls-position="right"
              style="width: 120px"
            />
          </el-form-item>
          <el-form-item label="住址" prop="address">
            <el-input v-model="formData.address" placeholder="住址" clearable type="textarea" :rows="2" />
          </el-form-item>
          <el-form-item label="既往病史" prop="medicalHistory">
            <el-input v-model="formData.medicalHistory" placeholder="既往病史" clearable type="textarea" :rows="2" />
          </el-form-item>
          <el-form-item label="过敏史" prop="allergyHistory">
            <el-input v-model="formData.allergyHistory" placeholder="过敏史" clearable type="textarea" :rows="2" />
          </el-form-item>
        </el-form>
      </template>
    </BaseManageView>

    <el-dialog v-model="detailVisible" title="患者详情" width="560px" destroy-on-close>
      <el-descriptions v-if="detailRow" :column="1" border>
        <el-descriptions-item label="用户名">{{ detailRow.username ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="真实姓名">{{ detailRow.realName ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detailRow.phone ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="年龄">{{ detailRow.age ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="住址">{{ detailRow.address ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="既往病史">{{ detailRow.medicalHistory ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="过敏史">{{ detailRow.allergyHistory ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailRow.createTime ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ detailRow.updateTime ?? '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import BaseManageView from '../../../components/admin/BaseManageView.vue'
import {
  getPatientInfoListApi,
  getPatientUserOptionsApi,
  createPatientInfoApi,
  updatePatientInfoApi,
  deletePatientInfoApi,
  type AdminPatientInfoItem,
  type AdminPatientInfoListParams,
  type AdminPatientInfoSaveParams,
  type UserOptionItem,
} from '../../../api/admin'

const baseRef = ref<InstanceType<typeof BaseManageView> | null>(null)
const formRef = ref<FormInstance | null>(null)
const patientUserOptions = ref<UserOptionItem[]>([])
const detailVisible = ref(false)
const detailRow = ref<AdminPatientInfoItem | null>(null)

const formRules: FormRules = {
  userId: [{ required: true, message: '请选择用户', trigger: 'change' }],
}

function loadList() {
  if (!baseRef.value) return
  baseRef.value.setLoading(true)
  const searchForm = baseRef.value.searchForm as AdminPatientInfoListParams
  const pagination = baseRef.value.pagination
  const params = {
    current: pagination.current,
    size: pagination.size,
    username: searchForm?.username || undefined,
    realName: searchForm?.realName || undefined,
    age: searchForm?.age ?? undefined,
  }
  getPatientInfoListApi(params)
    .then((res) => {
      if (!baseRef.value) return
      baseRef.value.setDataList((res?.records ?? []) as unknown as Record<string, unknown>[])
      baseRef.value.setPagination({
        current: res?.current ?? 1,
        size: res?.size ?? 10,
        total: res?.total ?? 0,
      })
    })
    .catch(() => {
      if (baseRef.value) baseRef.value.setDataList([])
    })
    .finally(() => {
      baseRef.value?.setLoading(false)
    })
}

function handleSearch() {
  if (baseRef.value) baseRef.value.pagination.current = 1
  loadList()
}

async function handleAdd() {
  patientUserOptions.value = (await getPatientUserOptionsApi()) ?? []
}

function handleEdit(row: AdminPatientInfoItem) {
  baseRef.value?.handleEdit(row as unknown as Record<string, unknown>)
}

async function handleDelete(row: AdminPatientInfoItem) {
  try {
    await ElMessageBox.confirm(
      `确定删除患者「${row.realName || row.username || row.id}」的扩展信息吗？`,
      '提示',
      { type: 'warning' }
    )
    await deletePatientInfoApi(row.id)
    ElMessage.success('删除成功')
    loadList()
  } catch (e: unknown) {
    if ((e as { type?: string })?.type !== 'cancel') {
      const msg = (e as { response?: { data?: { message?: string } } })?.response?.data?.message
      ElMessage.error(msg ?? '删除失败')
    }
  }
}

function openDetail(row: AdminPatientInfoItem) {
  detailRow.value = row
  detailVisible.value = true
}

async function handleSubmit(payload: Record<string, unknown>) {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  const isEdit = !!payload.isEdit
  const formData = payload as unknown as AdminPatientInfoSaveParams & { id?: number }
  try {
    const data: AdminPatientInfoSaveParams = {
      userId: formData.userId as number,
      age: (formData.age as number) ?? undefined,
      address: (formData.address as string) || undefined,
      medicalHistory: (formData.medicalHistory as string) || undefined,
      allergyHistory: (formData.allergyHistory as string) || undefined,
    }
    if (isEdit && formData.id) {
      await updatePatientInfoApi(formData.id, data)
      ElMessage.success('修改成功')
    } else {
      await createPatientInfoApi(data)
      ElMessage.success('新增成功')
    }
    baseRef.value!.dialogVisible = false
    loadList()
  } catch (e: unknown) {
    const msg = (e as { response?: { data?: { message?: string } } })?.response?.data?.message
    ElMessage.error(msg ?? '操作失败')
  }
}

onMounted(() => {
  if (baseRef.value) {
    baseRef.value.searchForm = {
      username: '',
      realName: '',
      age: undefined,
    }
  }
  loadList()
})
</script>

<style scoped>
.patient-info-manager {
  min-height: 100%;
  padding-bottom: 32px;
}
</style>
