<template>
  <BaseManageView
    ref="baseRef"
    title="操作日志管理"
    :show-add-button="false"
    :show-selection="true"
    @search="handleSearch"
    @delete="(row) => handleDelete(row as unknown as OperationLog)"
    @selection-change="handleSelectionChange"
  >
    <!-- 自定义搜索表单 -->
    <template #search-form="{ searchForm }">
      <el-form-item label="用户名">
        <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
      </el-form-item>
      <el-form-item label="操作类型">
        <el-input v-model="searchForm.operationType" placeholder="请输入操作类型" clearable />
      </el-form-item>
      <el-form-item label="操作模块">
        <el-input v-model="searchForm.operationModule" placeholder="请输入操作模块" clearable />
      </el-form-item>
      <el-form-item label="操作状态">
        <el-select
          v-model="searchForm.status"
          placeholder="请选择状态"
          clearable
          style="width: 150px"
        >
          <el-option label="成功" :value="1" />
          <el-option label="失败" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item label="开始时间">
        <el-date-picker
          v-model="searchForm.startTime"
          type="datetime"
          placeholder="选择开始时间"
          format="YYYY-MM-DD HH:mm:ss"
          value-format="YYYY-MM-DD HH:mm:ss"
          clearable
        />
      </el-form-item>
      <el-form-item label="结束时间">
        <el-date-picker
          v-model="searchForm.endTime"
          type="datetime"
          placeholder="选择结束时间"
          format="YYYY-MM-DD HH:mm:ss"
          value-format="YYYY-MM-DD HH:mm:ss"
          clearable
        />
      </el-form-item>
      <el-form-item>
        <el-button
          type="danger"
          :disabled="!baseRef?.selectedRows || baseRef.selectedRows.length === 0"
          @click="handleBatchDelete"
        >
          批量删除
        </el-button>
      </el-form-item>
    </template>

    <!-- 自定义表格列 -->
    <template #table-columns>
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="operationType" label="操作类型" width="120" />
      <el-table-column prop="operationModule" label="操作模块" width="120" />
      <el-table-column
        prop="operationDesc"
        label="操作描述"
        min-width="150"
        show-overflow-tooltip
      />
      <el-table-column prop="requestMethod" label="请求方法" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.requestMethod" :type="getMethodTagType(row.requestMethod)" size="small">
            {{ row.requestMethod }}
          </el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="requestUrl" label="请求URL" min-width="200" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '成功' : '失败' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="executionTime" label="执行时间(ms)" width="120">
        <template #default="{ row }">
          {{ row.executionTime ? `${row.executionTime}ms` : '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="ipAddress" label="IP地址" width="140" />
      <el-table-column prop="createTime" label="创建时间" width="180" />
    </template>

    <!-- 自定义操作按钮 -->
    <template #actions="{ row }">
      <el-button type="primary" link size="small" @click="handleView(row as OperationLog)"
        >查看详情</el-button
      >
      <el-button type="danger" link size="small" @click="handleDelete(row as OperationLog)"
        >删除</el-button
      >
    </template>
  </BaseManageView>

  <!-- 详情对话框 -->
  <DetailDialog v-model="detailDialogVisible" title="操作日志详情" width="1000px">
    <el-descriptions :column="2" border>
      <el-descriptions-item label="日志ID">{{ detailData.id }}</el-descriptions-item>
      <el-descriptions-item label="用户ID">{{ detailData.userId || '-' }}</el-descriptions-item>
      <el-descriptions-item label="用户名">{{ detailData.username || '-' }}</el-descriptions-item>
      <el-descriptions-item label="操作状态">
        <el-tag :type="detailData.status === 1 ? 'success' : 'danger'">
          {{ detailData.status === 1 ? '成功' : '失败' }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="操作类型">{{
        detailData.operationType || '-'
      }}</el-descriptions-item>
      <el-descriptions-item label="操作模块">{{
        detailData.operationModule || '-'
      }}</el-descriptions-item>
      <el-descriptions-item label="操作描述" :span="2">{{
        detailData.operationDesc || '-'
      }}</el-descriptions-item>
      <el-descriptions-item label="请求方法">{{
        detailData.requestMethod || '-'
      }}</el-descriptions-item>
      <el-descriptions-item label="执行时间">
        {{ detailData.executionTime ? `${detailData.executionTime}ms` : '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="请求URL" :span="2">
        <span style="word-break: break-all">{{ detailData.requestUrl || '-' }}</span>
      </el-descriptions-item>
      <el-descriptions-item label="请求参数" :span="2">
        <JsonViewer :json="detailData.requestParams" />
      </el-descriptions-item>
      <el-descriptions-item label="响应数据" :span="2">
        <JsonViewer :json="detailData.responseData" />
      </el-descriptions-item>
      <el-descriptions-item label="IP地址">{{ detailData.ipAddress || '-' }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
      <el-descriptions-item label="用户代理" :span="2">
        <span style="word-break: break-all; font-size: 12px">{{
          detailData.userAgent || '-'
        }}</span>
      </el-descriptions-item>
      <el-descriptions-item v-if="detailData.errorMsg" label="错误信息" :span="2">
        <el-alert :title="detailData.errorMsg" type="error" :closable="false" />
      </el-descriptions-item>
    </el-descriptions>
  </DetailDialog>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import BaseManageView from '../../../components/admin/BaseManageView.vue'
import DetailDialog from '../../../components/admin/DetailDialog.vue'
import JsonViewer from '../../../components/admin/JsonViewer.vue'
import {
  getOperationLogListApi,
  getOperationLogByIdApi,
  deleteOperationLogApi,
  batchDeleteOperationLogApi,
  type OperationLog,
  type OperationLogListParams,
} from '../../../api/admin/operationLog'

const baseRef = ref<InstanceType<typeof BaseManageView>>()
const detailDialogVisible = ref(false)
const detailData = ref<OperationLog>({} as OperationLog)

// 加载操作日志列表
const loadLogList = async () => {
  if (!baseRef.value) return

  baseRef.value.setLoading(true)
  try {
    const searchForm = baseRef.value.searchForm as OperationLogListParams
    const pagination = baseRef.value.pagination

    const params: OperationLogListParams = {
      current: pagination.current,
      size: pagination.size,
      ...searchForm,
    }
    const result = await getOperationLogListApi(params)
    baseRef.value.setDataList(result.records as unknown as Record<string, unknown>[])
    baseRef.value.setPagination({
      current: result.current,
      size: result.size,
      total: result.total,
    })
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : '加载操作日志列表失败'
    ElMessage.error(message)
  } finally {
    baseRef.value.setLoading(false)
  }
}

// 搜索
const handleSearch = () => {
  loadLogList()
}

// 删除
const handleDelete = async (row: OperationLog) => {
  try {
    await ElMessageBox.confirm(`确定要删除这条操作日志吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await deleteOperationLogApi(row.id)
    ElMessage.success('删除成功')
    loadLogList()
  } catch (error: unknown) {
    if (error !== 'cancel') {
      const message = error instanceof Error ? error.message : '删除失败'
      ElMessage.error(message)
    }
  }
}

// 选择变化
const handleSelectionChange = () => {
  // 选择变化已由 BaseManageView 处理
}

// 批量删除
const handleBatchDelete = async () => {
  if (!baseRef.value || !baseRef.value.selectedRows || baseRef.value.selectedRows.length === 0) {
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${baseRef.value.selectedRows.length} 条操作日志吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      },
    )
    const ids = (baseRef.value.selectedRows as unknown as OperationLog[]).map((row) => row.id)
    await batchDeleteOperationLogApi(ids)
    ElMessage.success('批量删除成功')
    baseRef.value.selectedRows = []
    loadLogList()
  } catch (error: unknown) {
    if (error !== 'cancel') {
      const message = error instanceof Error ? error.message : '批量删除失败'
      ElMessage.error(message)
    }
  }
}

// 查看详情
const handleView = async (row: OperationLog) => {
  try {
    const detail = await getOperationLogByIdApi(row.id)
    detailData.value = detail
    detailDialogVisible.value = true
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : '获取详情失败'
    ElMessage.error(message)
  }
}

// 获取请求方法标签类型
const getMethodTagType = (method: string) => {
  const map: Record<string, string> = {
    GET: '',
    POST: 'success',
    PUT: 'warning',
    DELETE: 'danger',
  }
  return map[method] || ''
}

onMounted(() => {
  // 初始化搜索表单
  if (baseRef.value) {
    baseRef.value.searchForm = {
      username: '',
      operationType: '',
      operationModule: '',
      status: undefined,
      startTime: '',
      endTime: '',
    }
  }
  loadLogList()
})
</script>

<style scoped></style>
