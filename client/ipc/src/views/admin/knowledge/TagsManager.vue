<template>
  <BaseManageView
    ref="baseRef"
    title="知识库标签管理"
    add-permission="knowledge:tag:add"
    edit-permission="knowledge:tag:edit"
    delete-permission="knowledge:tag:delete"
    @search="handleSearch"
    @add="handleAdd"
    @edit="handleEdit"
    @delete="(row) => handleDelete(row as KnowledgeTag)"
    @submit="handleSubmit"
  >
    <template #search-form="{ searchForm }">
      <el-form-item label="标签名称">
        <el-input
          v-model="searchForm.tagName"
          placeholder="请输入标签名称"
          clearable
          style="width: 180px"
        />
      </el-form-item>
    </template>

    <template #table-columns>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="tagName" label="标签名称" min-width="140" />
      <el-table-column prop="tagColor" label="颜色" width="120">
        <template #default="{ row }">
          <span
            v-if="(row as KnowledgeTag).tagColor"
            class="tag-color-dot"
            :style="{ background: (row as KnowledgeTag).tagColor }"
          />
          <span>{{ (row as KnowledgeTag).tagColor || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="useCount" label="使用次数" width="100" />
      <el-table-column prop="createTime" label="创建时间" width="180" />
    </template>

    <template #dialog-form="{ formData, dialogVisible }">
      <el-form v-if="dialogVisible" :model="formData" label-width="90px" style="max-width: 420px">
        <el-form-item label="标签名称" required>
          <el-input v-model="formData.tagName" placeholder="请输入标签名称" />
        </el-form-item>
        <el-form-item label="标签颜色">
          <el-input v-model="formData.tagColor" placeholder="如 #409EFF 或 颜色名" />
        </el-form-item>
      </el-form>
    </template>
  </BaseManageView>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import BaseManageView from '../../../components/admin/BaseManageView.vue'
import {
  getTagListApi,
  createTagApi,
  updateTagApi,
  deleteTagApi,
  type KnowledgeTag,
  type KnowledgeTagListParams,
  type KnowledgeTagCreateParams,
  type KnowledgeTagUpdateParams,
} from '../../../api/admin/knowledge'

const baseRef = ref<InstanceType<typeof BaseManageView>>()

const loadList = async () => {
  if (!baseRef.value) return
  baseRef.value.setLoading(true)
  try {
    const searchForm = baseRef.value.searchForm as KnowledgeTagListParams
    const pagination = baseRef.value.pagination
    const params: KnowledgeTagListParams = {
      current: pagination.current,
      size: pagination.size,
      tagName: searchForm.tagName as string | undefined,
    }
    const result = await getTagListApi(params)
    baseRef.value.setDataList(result.records as unknown as Record<string, unknown>[])
    baseRef.value.setPagination({
      current: result.current,
      size: result.size,
      total: result.total,
    })
  } catch (error: unknown) {
    ElMessage.error(error instanceof Error ? error.message : '加载列表失败')
  } finally {
    baseRef.value.setLoading(false)
  }
}

const handleSearch = () => {
  loadList()
}

const handleAdd = () => {}

const handleEdit = () => {}

const handleDelete = async (row: KnowledgeTag) => {
  try {
    await ElMessageBox.confirm(`确定要删除标签「${row.tagName || row.id}」吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await deleteTagApi(row.id)
    ElMessage.success('删除成功')
    loadList()
  } catch (error: unknown) {
    if (error !== 'cancel') {
      ElMessage.error(error instanceof Error ? error.message : '删除失败')
    }
  }
}

const handleSubmit = async (payload: Record<string, unknown> & { isEdit?: boolean }) => {
  if (!baseRef.value) return
  const { isEdit } = payload
  const id = payload.id as number | undefined
  try {
    if (isEdit && id != null) {
      const data: KnowledgeTagUpdateParams = {
        tagName: payload.tagName as string,
        tagColor: payload.tagColor as string,
      }
      await updateTagApi(id, data)
      ElMessage.success('更新成功')
    } else {
      const data: KnowledgeTagCreateParams = {
        tagName: (payload.tagName as string) || '',
        tagColor: payload.tagColor as string,
      }
      await createTagApi(data)
      ElMessage.success('新增成功')
    }
    baseRef.value.dialogVisible = false
    loadList()
  } catch (error: unknown) {
    ElMessage.error(error instanceof Error ? error.message : '保存失败')
  }
}

onMounted(() => {
  if (baseRef.value) {
    baseRef.value.searchForm = { tagName: '' }
  }
  loadList()
})
</script>

<style scoped>
.tag-color-dot {
  display: inline-block;
  width: 14px;
  height: 14px;
  border-radius: 4px;
  margin-right: 6px;
  vertical-align: middle;
}
</style>
