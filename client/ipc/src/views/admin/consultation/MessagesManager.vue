<template>
  <div class="manage-view">
    <el-card>
      <template #header>
        <span>问诊消息管理</span>
      </template>

      <!-- 搜索：按会话ID查询消息 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="会话ID">
          <el-input-number
            v-model="searchForm.sessionId"
            placeholder="请输入会话ID"
            :min="1"
            clearable
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-alert
        v-if="!searchForm.sessionId"
        title="请输入会话ID后点击查询，查看该会话下的问诊消息"
        type="info"
        :closable="false"
        show-icon
        class="tip-alert"
      />

      <el-table
        v-else
        :data="dataList"
        v-loading="loading"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="id" label="消息ID" width="90" />
        <el-table-column prop="sessionId" label="会话ID" width="90" />
        <el-table-column prop="senderType" label="发送者" width="100">
          <template #default="{ row }">
            <el-tag :type="row.senderType === 1 ? 'success' : row.senderType === 2 ? 'primary' : 'info'">
              {{ senderTypeText(row.senderType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="messageType" label="类型" width="80">
          <template #default="{ row }">
            {{ messageTypeText(row.messageType) }}
          </template>
        </el-table-column>
        <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="aiModel" label="AI模型" width="100">
          <template #default="{ row }">
            {{ row.aiModel || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="isRead" label="已读" width="70">
          <template #default="{ row }">
            {{ row.isRead === 1 ? '是' : '否' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button
              v-permission="'consultation:message:delete'"
              type="danger"
              link
              size="small"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <AdminPagination
        v-if="searchForm.sessionId"
        :current="pagination.current"
        :size="pagination.size"
        :total="pagination.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import AdminPagination from '../../../components/admin/AdminPagination.vue'
import {
  getConsultationMessageListApi,
  deleteConsultationMessageApi,
  type ConsultationMessage,
  type ConsultationMessageListParams,
} from '../../../api/admin/consultation'
import { ElMessageBox } from 'element-plus'

const loading = ref(false)
const dataList = ref<ConsultationMessage[]>([])
const searchForm = reactive<{ sessionId?: number }>({})
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0,
})

const loadMessageList = async () => {
  const sessionId = searchForm.sessionId
  if (!sessionId) {
    dataList.value = []
    pagination.total = 0
    return
  }

  loading.value = true
  try {
    const params: ConsultationMessageListParams = {
      current: pagination.current,
      size: pagination.size,
    }
    const result = await getConsultationMessageListApi(sessionId, params)
    dataList.value = result.records || []
    pagination.total = result.total || 0
    pagination.current = result.current || 1
    pagination.size = result.size || 10
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : '加载消息列表失败'
    ElMessage.error(message)
    dataList.value = []
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadMessageList()
}

const handleReset = () => {
  searchForm.sessionId = undefined
  pagination.current = 1
  pagination.total = 0
  dataList.value = []
}

const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.current = 1
  loadMessageList()
}

const handleCurrentChange = (current: number) => {
  pagination.current = current
  loadMessageList()
}

const senderTypeText = (senderType: number) => {
  const map: Record<number, string> = {
    1: '患者',
    2: '医生',
    3: 'AI',
  }
  return map[senderType] ?? '未知'
}

const messageTypeText = (messageType: number) => {
  const map: Record<number, string> = {
    1: '文本',
    2: '图片',
    3: '语音',
    4: '视频',
  }
  return map[messageType] ?? '未知'
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

const handleDelete = async (row: ConsultationMessage) => {
  try {
    await ElMessageBox.confirm('确定要删除该消息吗？删除后不可恢复。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await deleteConsultationMessageApi(row.id)
    ElMessage.success('删除成功')
    loadMessageList()
  } catch (error: unknown) {
    if ((error as string) !== 'cancel') {
      const message = error instanceof Error ? error.message : '删除失败'
      ElMessage.error(message)
    }
  }
}

onMounted(() => {
  // 不自动加载，等用户输入会话ID后查询
})
</script>

<style scoped>
.manage-view {
  padding: 0;
}
.search-form {
  margin-bottom: 16px;
}
.tip-alert {
  margin-bottom: 16px;
}
</style>
