<template>
  <div class="consultation-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>问诊管理</span>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="患者姓名">
          <el-input v-model="searchForm.patientName" placeholder="请输入患者姓名" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="问诊标题">
          <el-input v-model="searchForm.title" placeholder="请输入问诊标题" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 150px">
            <el-option label="进行中" :value="0" />
            <el-option label="已结束" :value="1" />
            <el-option label="已取消" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="会话类型">
          <el-select v-model="searchForm.sessionType" placeholder="请选择类型" clearable style="width: 150px">
            <el-option label="AI问诊" :value="1" />
            <el-option label="医生问诊" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="sessionNo" label="会话编号" width="180" />
        <el-table-column prop="patientName" label="患者姓名" width="120" />
        <el-table-column prop="title" label="问诊标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="sessionType" label="会话类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.sessionType === 1 ? 'info' : 'success'">
              {{ row.sessionType === 1 ? 'AI问诊' : '医生问诊' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="unreadCount" label="未读消息" width="100">
          <template #default="{ row }">
            <el-badge v-if="row.unreadCount && row.unreadCount > 0" :value="row.unreadCount" class="item">
              <span>{{ row.unreadCount }}</span>
            </el-badge>
            <span v-else>0</span>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="endTime" label="结束时间" width="180" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleViewDetail(row)">
              查看详情
            </el-button>
            <el-button v-if="row.status === 0" type="success" link size="small" @click="handleEndConsultation(row)">
              结束问诊
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

    <!-- 问诊详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="问诊详情" width="800px">
      <el-descriptions v-if="currentConsultation" :column="2" border>
        <el-descriptions-item label="会话编号">{{ currentConsultation.sessionNo }}</el-descriptions-item>
        <el-descriptions-item label="患者姓名">{{ currentConsultation.patientName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="问诊标题" :span="2">{{ currentConsultation.title || '-' }}</el-descriptions-item>
        <el-descriptions-item label="会话类型">
          <el-tag :type="currentConsultation.sessionType === 1 ? 'info' : 'success'">
            {{ currentConsultation.sessionType === 1 ? 'AI问诊' : '医生问诊' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentConsultation.status)">
            {{ getStatusText(currentConsultation.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ currentConsultation.startTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ currentConsultation.endTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="未读消息数">{{ currentConsultation.unreadCount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentConsultation.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getConsultationListApi, type Consultation, type ConsultationListParams } from '../../api/doctor'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref<Consultation[]>([])
const searchForm = reactive<ConsultationListParams>({
  patientName: '',
  title: '',
  status: undefined,
  sessionType: undefined,
})
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0,
})
const detailDialogVisible = ref(false)
const currentConsultation = ref<Consultation | null>(null)

const getStatusText = (status: number) => {
  const statusMap: Record<number, string> = {
    0: '进行中',
    1: '已结束',
    2: '已取消',
  }
  return statusMap[status] || '未知'
}

const getStatusType = (status: number): 'success' | 'warning' | 'danger' | 'info' => {
  const typeMap: Record<number, 'success' | 'warning' | 'danger' | 'info'> = {
    0: 'warning',
    1: 'success',
    2: 'danger',
  }
  return typeMap[status] || 'info'
}

const loadData = async () => {
  try {
    loading.value = true
    const params: ConsultationListParams = {
      current: pagination.current,
      size: pagination.size,
      ...searchForm,
    }
    const response = await getConsultationListApi(params)
    tableData.value = response.records
    pagination.total = response.total
    pagination.current = response.current
    pagination.size = response.size
  } catch (error: any) {
    ElMessage.error(error.message || '加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.patientName = ''
  searchForm.title = ''
  searchForm.status = undefined
  searchForm.sessionType = undefined
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

const handleViewDetail = (row: Consultation) => {
  currentConsultation.value = row
  detailDialogVisible.value = true
}

const handleEndConsultation = async (row: Consultation) => {
  try {
    await ElMessageBox.confirm('确定要结束此次问诊吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    // TODO: 调用结束问诊API
    ElMessage.success('问诊已结束')
    loadData()
  } catch (error) {
    // 用户取消
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.consultation-list {
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

.item {
  margin-top: 10px;
  margin-right: 40px;
}
</style>
