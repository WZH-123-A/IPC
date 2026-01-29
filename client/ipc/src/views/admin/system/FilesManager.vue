<template>
  <BaseManageView
    ref="baseRef"
    title="文件管理"
    :show-add-button="false"
    :show-selection="true"
    delete-permission="system:file:delete"
    @search="handleSearch"
    @delete="(row) => handleDelete(row as unknown as SysFile)"
    @selection-change="handleSelectionChange"
  >
    <!-- 搜索表单 -->
    <template #search-form="{ searchForm }">
      <el-form-item label="文件名称">
        <el-input
          v-model="searchForm.fileName"
          placeholder="请输入文件名称"
          clearable
          style="width: 180px"
        />
      </el-form-item>
      <el-form-item label="文件类型">
        <el-select
          v-model="searchForm.fileType"
          placeholder="请选择类型"
          clearable
          style="width: 140px"
        >
          <el-option label="图片" value="image" />
          <el-option label="视频" value="video" />
          <el-option label="文档" value="document" />
          <el-option label="音频" value="audio" />
          <el-option label="其他" value="other" />
        </el-select>
      </el-form-item>
      <el-form-item label="业务类型">
        <el-select
          v-model="searchForm.businessType"
          placeholder="请选择业务类型"
          clearable
          style="width: 140px"
        >
          <el-option label="诊断" value="diagnosis" />
          <el-option label="问诊" value="consultation" />
          <el-option label="知识库" value="knowledge" />
          <el-option label="通用" value="common" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button
          v-permission="'system:file:batch-delete'"
          type="danger"
          :disabled="!baseRef?.selectedRows || baseRef.selectedRows.length === 0"
          @click="handleBatchDelete"
        >
          批量删除
        </el-button>
      </el-form-item>
    </template>

    <!-- 表格列 -->
    <template #table-columns>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="fileName" label="文件名称" min-width="160" show-overflow-tooltip />
      <el-table-column prop="fileType" label="类型" width="90">
        <template #default="{ row }">
          <el-tag v-if="row.fileType" size="small">{{ row.fileType }}</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="fileSize" label="大小" width="100">
        <template #default="{ row }">
          {{ formatFileSize(row.fileSize) }}
        </template>
      </el-table-column>
      <el-table-column prop="businessType" label="业务类型" width="100" />
      <el-table-column prop="uploadUserId" label="上传用户ID" width="110" />
      <el-table-column prop="createTime" label="创建时间" width="180" />
    </template>

    <template #actions="{ row }">
      <el-button
        v-permission="'system:file:view'"
        type="primary"
        link
        size="small"
        @click="handleView(row as SysFile)"
      >
        查看
      </el-button>
      <el-button
        v-if="(row as SysFile).fileUrl"
        type="primary"
        link
        size="small"
        @click="handlePreview(row as SysFile)"
      >
        预览
      </el-button>
      <el-button
        v-permission="'system:file:delete'"
        type="danger"
        link
        size="small"
        @click="handleDelete(row as SysFile)"
      >
        删除
      </el-button>
    </template>
  </BaseManageView>

  <!-- 详情对话框 -->
  <DetailDialog v-model="detailDialogVisible" title="文件详情" width="640px">
    <el-descriptions :column="1" border>
      <el-descriptions-item label="文件ID">{{ detailData.id }}</el-descriptions-item>
      <el-descriptions-item label="文件名称">{{ detailData.fileName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="存储路径">{{ detailData.filePath || '-' }}</el-descriptions-item>
      <el-descriptions-item label="访问URL">
        <span v-if="detailData.fileUrl" class="file-url">{{ detailData.fileUrl }}</span>
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="文件类型">{{ detailData.fileType || '-' }}</el-descriptions-item>
      <el-descriptions-item label="文件大小">{{ formatFileSize(detailData.fileSize) }}</el-descriptions-item>
      <el-descriptions-item label="MIME类型">{{ detailData.mimeType || '-' }}</el-descriptions-item>
      <el-descriptions-item label="上传用户ID">{{ detailData.uploadUserId ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="业务类型">{{ detailData.businessType || '-' }}</el-descriptions-item>
      <el-descriptions-item label="业务ID">{{ detailData.businessId ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ detailData.createTime || '-' }}</el-descriptions-item>
    </el-descriptions>
  </DetailDialog>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import BaseManageView from '../../../components/admin/BaseManageView.vue'
import DetailDialog from '../../../components/admin/DetailDialog.vue'
import {
  getFileListApi,
  getFileByIdApi,
  deleteFileApi,
  batchDeleteFileApi,
  type SysFile,
  type SysFileListParams,
} from '../../../api/admin/file'

const baseRef = ref<InstanceType<typeof BaseManageView>>()
const detailDialogVisible = ref(false)
const detailData = ref<SysFile>({} as SysFile)

function formatFileSize(bytes?: number): string {
  if (bytes == null || bytes === 0) return '-'
  if (bytes < 1024) return `${bytes} B`
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(2)} KB`
  return `${(bytes / 1024 / 1024).toFixed(2)} MB`
}

const loadList = async () => {
  if (!baseRef.value) return
  baseRef.value.setLoading(true)
  try {
    const searchForm = baseRef.value.searchForm as SysFileListParams
    const pagination = baseRef.value.pagination
    const params: SysFileListParams = {
      current: pagination.current,
      size: pagination.size,
      ...searchForm,
    }
    const result = await getFileListApi(params)
    baseRef.value.setDataList(result.records as unknown as Record<string, unknown>[])
    baseRef.value.setPagination({
      current: result.current,
      size: result.size,
      total: result.total,
    })
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : '加载文件列表失败'
    ElMessage.error(message)
  } finally {
    baseRef.value.setLoading(false)
  }
}

const handleSearch = () => {
  loadList()
}

const handleSelectionChange = () => {}

const handleDelete = async (row: SysFile) => {
  try {
    await ElMessageBox.confirm(`确定要删除文件记录「${row.fileName || row.id}」吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await deleteFileApi(row.id)
    ElMessage.success('删除成功')
    loadList()
  } catch (error: unknown) {
    if (error !== 'cancel') {
      const message = error instanceof Error ? error.message : '删除失败'
      ElMessage.error(message)
    }
  }
}

const handleBatchDelete = async () => {
  if (!baseRef.value?.selectedRows?.length) return
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${baseRef.value.selectedRows.length} 条文件记录吗？`,
      '提示',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )
    const ids = (baseRef.value.selectedRows as unknown as SysFile[]).map((r) => r.id)
    await batchDeleteFileApi(ids)
    ElMessage.success('批量删除成功')
    baseRef.value.selectedRows = []
    loadList()
  } catch (error: unknown) {
    if (error !== 'cancel') {
      ElMessage.error(error instanceof Error ? error.message : '批量删除失败')
    }
  }
}

const handleView = async (row: SysFile) => {
  try {
    const detail = await getFileByIdApi(row.id)
    detailData.value = detail
    detailDialogVisible.value = true
  } catch (error: unknown) {
    ElMessage.error(error instanceof Error ? error.message : '获取详情失败')
  }
}

/** 获取上传文件所在的后端地址（预览用），避免用前端 origin 导致 404 */
function getUploadOrigin(): string {
  const apiBase = import.meta.env.VITE_API_BASE_URL || ''
  if (apiBase.startsWith('http')) {
    try {
      return new URL(apiBase).origin
    } catch {
      return window.location.origin
    }
  }
  // 开发环境下前端 5173、后端 8080，预览必须请求后端
  if (import.meta.env.DEV) {
    return 'http://localhost:8080'
  }
  return window.location.origin
}

const handlePreview = (row: SysFile) => {
  if (row.fileUrl) {
    const url = row.fileUrl.startsWith('http') ? row.fileUrl : `${getUploadOrigin()}${row.fileUrl}`
    window.open(url, '_blank')
  }
}

onMounted(() => {
  if (baseRef.value) {
    baseRef.value.searchForm = {
      fileName: '',
      fileType: '',
      businessType: '',
    }
  }
  loadList()
})
</script>

<style scoped>
.file-url {
  word-break: break-all;
  font-size: 12px;
}
</style>
