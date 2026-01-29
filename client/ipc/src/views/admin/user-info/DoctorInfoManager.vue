<template>
  <div class="doctor-info-manager">
    <BaseManageView
      ref="baseRef"
      title="医生信息管理"
      :show-add-button="true"
      :show-selection="false"
      add-permission="user-info:doctor:add"
      edit-permission="user-info:doctor:edit"
      delete-permission="user-info:doctor:delete"
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
        <el-form-item label="医院">
          <el-input v-model="searchForm.hospital" placeholder="医院" clearable style="width: 140px" />
        </el-form-item>
        <el-form-item label="科室">
          <el-input v-model="searchForm.department" placeholder="科室" clearable style="width: 140px" />
        </el-form-item>
      </template>

      <template #table-columns>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="username" label="用户名" width="110" show-overflow-tooltip />
        <el-table-column prop="realName" label="真实姓名" width="100" show-overflow-tooltip />
        <el-table-column prop="phone" label="手机号" width="120" show-overflow-tooltip />
        <el-table-column prop="hospital" label="所属医院" min-width="140" show-overflow-tooltip />
        <el-table-column prop="department" label="科室" width="100" show-overflow-tooltip />
        <el-table-column prop="title" label="职称" width="100" show-overflow-tooltip />
        <el-table-column prop="specialty" label="擅长领域" min-width="120" show-overflow-tooltip />
        <el-table-column prop="workYears" label="从业年限" width="90" align="center" />
        <el-table-column prop="createTime" label="创建时间" width="170" show-overflow-tooltip />
      </template>

      <template #actions="{ row }">
        <el-button type="primary" link size="small" @click="openDetail(row as AdminDoctorInfoItem)">查看</el-button>
        <el-button
          v-permission="'user-info:doctor:edit'"
          type="primary"
          link
          size="small"
          @click="handleEdit(row as AdminDoctorInfoItem)"
        >
          编辑
        </el-button>
        <el-button
          v-permission="'user-info:doctor:delete'"
          type="danger"
          link
          size="small"
          @click="handleDelete(row as AdminDoctorInfoItem)"
        >
          删除
        </el-button>
      </template>

      <template #dialog-form="{ formData }">
        <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
          <el-form-item label="用户" prop="userId">
            <el-select
              v-model="formData.userId"
              placeholder="请选择用户（已具医生角色且未填写信息）"
              filterable
              style="width: 100%"
              :disabled="!!formData.id"
            >
              <el-option
                v-for="u in doctorUserOptions"
                :key="u.id"
                :label="`${u.username ?? ''} ${u.realName ? `(${u.realName})` : ''}`.trim() || String(u.id)"
                :value="u.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="所属医院" prop="hospital">
            <el-input v-model="formData.hospital" placeholder="所属医院" clearable />
          </el-form-item>
          <el-form-item label="科室" prop="department">
            <el-input v-model="formData.department" placeholder="科室" clearable />
          </el-form-item>
          <el-form-item label="职称" prop="title">
            <el-input v-model="formData.title" placeholder="如主任医师" clearable />
          </el-form-item>
          <el-form-item label="擅长领域" prop="specialty">
            <el-input v-model="formData.specialty" placeholder="擅长领域" clearable type="textarea" :rows="2" />
          </el-form-item>
          <el-form-item label="资格证编号" prop="licenseNo">
            <el-input v-model="formData.licenseNo" placeholder="医师资格证编号" clearable />
          </el-form-item>
          <el-form-item label="从业年限" prop="workYears">
            <el-input-number
              v-model="formData.workYears"
              :min="0"
              :max="50"
              placeholder="年"
              controls-position="right"
              style="width: 120px"
            />
          </el-form-item>
        </el-form>
      </template>
    </BaseManageView>

    <el-dialog v-model="detailVisible" title="医生详情" width="560px" destroy-on-close>
      <el-descriptions v-if="detailRow" :column="1" border>
        <el-descriptions-item label="用户名">{{ detailRow.username ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="真实姓名">{{ detailRow.realName ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detailRow.phone ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="所属医院">{{ detailRow.hospital ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="科室">{{ detailRow.department ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="职称">{{ detailRow.title ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="擅长领域">{{ detailRow.specialty ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="医师资格证编号">{{ detailRow.licenseNo ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="从业年限">{{ detailRow.workYears ?? '-' }} 年</el-descriptions-item>
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
  getDoctorInfoListApi,
  getDoctorUserOptionsApi,
  createDoctorInfoApi,
  updateDoctorInfoApi,
  deleteDoctorInfoApi,
  type AdminDoctorInfoItem,
  type AdminDoctorInfoListParams,
  type AdminDoctorInfoSaveParams,
  type UserOptionItem,
} from '../../../api/admin'

const baseRef = ref<InstanceType<typeof BaseManageView> | null>(null)
const formRef = ref<FormInstance | null>(null)
const doctorUserOptions = ref<UserOptionItem[]>([])
const detailVisible = ref(false)
const detailRow = ref<AdminDoctorInfoItem | null>(null)

const formRules: FormRules = {
  userId: [{ required: true, message: '请选择用户', trigger: 'change' }],
}

function loadList() {
  if (!baseRef.value) return
  baseRef.value.setLoading(true)
  const searchForm = baseRef.value.searchForm as AdminDoctorInfoListParams
  const pagination = baseRef.value.pagination
  const params = {
    current: pagination.current,
    size: pagination.size,
    username: searchForm?.username || undefined,
    realName: searchForm?.realName || undefined,
    hospital: searchForm?.hospital || undefined,
    department: searchForm?.department || undefined,
  }
  getDoctorInfoListApi(params)
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
  doctorUserOptions.value = (await getDoctorUserOptionsApi()) ?? []
}

function handleEdit(row: AdminDoctorInfoItem) {
  baseRef.value?.handleEdit(row as unknown as Record<string, unknown>)
}

async function handleDelete(row: AdminDoctorInfoItem) {
  try {
    await ElMessageBox.confirm(
      `确定删除医生「${row.realName || row.username || row.id}」的扩展信息吗？`,
      '提示',
      { type: 'warning' }
    )
    await deleteDoctorInfoApi(row.id)
    ElMessage.success('删除成功')
    loadList()
  } catch (e: unknown) {
    if ((e as { type?: string })?.type !== 'cancel') {
      const msg = (e as { response?: { data?: { message?: string } } })?.response?.data?.message
      ElMessage.error(msg ?? '删除失败')
    }
  }
}

function openDetail(row: AdminDoctorInfoItem) {
  detailRow.value = row
  detailVisible.value = true
}

async function handleSubmit(payload: Record<string, unknown>) {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  const isEdit = !!payload.isEdit
  const formData = payload as unknown as AdminDoctorInfoSaveParams & { id?: number }
  try {
    const data: AdminDoctorInfoSaveParams = {
      userId: formData.userId as number,
      hospital: (formData.hospital as string) || undefined,
      department: (formData.department as string) || undefined,
      title: (formData.title as string) || undefined,
      specialty: (formData.specialty as string) || undefined,
      licenseNo: (formData.licenseNo as string) || undefined,
      workYears: (formData.workYears as number) ?? undefined,
    }
    if (isEdit && formData.id) {
      await updateDoctorInfoApi(formData.id, data)
      ElMessage.success('修改成功')
    } else {
      await createDoctorInfoApi(data)
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
      hospital: '',
      department: '',
    }
  }
  loadList()
})
</script>

<style scoped>
.doctor-info-manager {
  min-height: 100%;
  padding-bottom: 32px;
}
</style>
