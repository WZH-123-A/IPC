<template>
  <div class="patient-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>患者管理</span>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="患者姓名">
          <el-input v-model="searchForm.realName" placeholder="请输入患者姓名" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="{ row }">
            <span>{{ row.gender === 1 ? '男' : row.gender === 2 ? '女' : '未知' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="age" label="年龄" width="80" />
        <el-table-column prop="address" label="住址" min-width="150" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleViewDetail(row)">
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 患者详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="患者详情" width="800px">
      <el-descriptions v-if="currentPatient" :column="2" border>
        <el-descriptions-item label="用户名">{{ currentPatient.username }}</el-descriptions-item>
        <el-descriptions-item label="真实姓名">{{ currentPatient.realName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentPatient.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentPatient.email || '-' }}</el-descriptions-item>
        <el-descriptions-item label="性别">
          {{ currentPatient.gender === 1 ? '男' : currentPatient.gender === 2 ? '女' : '未知' }}
        </el-descriptions-item>
        <el-descriptions-item label="年龄">{{ currentPatient.age || '-' }}</el-descriptions-item>
        <el-descriptions-item label="住址" :span="2">{{ currentPatient.address || '-' }}</el-descriptions-item>
        <el-descriptions-item label="既往病史" :span="2">
          <div style="white-space: pre-wrap">{{ currentPatient.medicalHistory || '-' }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="过敏史" :span="2">
          <div style="white-space: pre-wrap">{{ currentPatient.allergyHistory || '-' }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentPatient.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getPatientListApi, type Patient, type PatientListParams } from '../../api/doctor'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref<Patient[]>([])
const searchForm = reactive<PatientListParams>({
  realName: '',
  username: '',
  phone: '',
})
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0,
})
const detailDialogVisible = ref(false)
const currentPatient = ref<Patient | null>(null)

const loadData = async () => {
  try {
    loading.value = true
    const params: PatientListParams = {
      current: pagination.current,
      size: pagination.size,
      ...searchForm,
    }
    const response = await getPatientListApi(params)
    tableData.value = response.records
    pagination.total = response.total
    pagination.current = response.current
    pagination.size = response.size
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : '加载数据失败'
    ElMessage.error(message)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.realName = ''
  searchForm.username = ''
  searchForm.phone = ''
  handleSearch()
}

const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.current = 1
  loadData()
}

const handleCurrentChange = (current: number) => {
  pagination.current = current
  loadData()
}

const handleViewDetail = (row: Patient) => {
  currentPatient.value = row
  detailDialogVisible.value = true
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.patient-list {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
