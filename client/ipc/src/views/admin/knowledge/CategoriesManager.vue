<template>
  <BaseManageView
    ref="baseRef"
    title="知识库分类管理"
    add-permission="knowledge:category:add"
    edit-permission="knowledge:category:edit"
    delete-permission="knowledge:category:delete"
    @search="handleSearch"
    @add="handleAdd"
    @edit="handleEdit"
    @delete="onDelete"
    @submit="handleSubmit"
  >
    <template #search-form="{ searchForm }">
      <el-form-item label="分类名称">
        <el-input
          v-model="searchForm.categoryName"
          placeholder="请输入分类名称"
          clearable
          style="width: 160px"
        />
      </el-form-item>
      <el-form-item label="分类编码">
        <el-input
          v-model="searchForm.categoryCode"
          placeholder="请输入分类编码"
          clearable
          style="width: 160px"
        />
      </el-form-item>
    </template>

    <template #table-columns>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="categoryCode" label="编码" width="120" />
      <el-table-column prop="categoryName" label="名称" min-width="140" />
      <el-table-column prop="parentId" label="父级ID" width="90" />
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column prop="description" label="描述" min-width="160" show-overflow-tooltip />
      <el-table-column prop="createTime" label="创建时间" width="180" />
    </template>

    <template #dialog-form="{ formData, dialogVisible }">
      <el-form v-if="dialogVisible" :model="formData" label-width="90px" style="max-width: 480px">
        <el-form-item label="分类编码" required>
          <el-input
            v-model="formData.categoryCode"
            placeholder="请输入分类编码"
            :disabled="!!(formData as { id?: number }).id"
          />
        </el-form-item>
        <el-form-item label="分类名称" required>
          <el-input v-model="formData.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="父级ID">
          <el-input-number v-model="formData.parentId" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="formData.sort" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="formData.icon" placeholder="图标 class 或 URL" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="formData.description" type="textarea" :rows="2" placeholder="选填" />
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
  getCategoryListApi,
  createCategoryApi,
  updateCategoryApi,
  deleteCategoryApi,
  type KnowledgeCategory,
  type KnowledgeCategoryListParams,
  type KnowledgeCategoryCreateParams,
  type KnowledgeCategoryUpdateParams,
} from '../../../api/admin/knowledge'

const baseRef = ref<InstanceType<typeof BaseManageView>>()

const loadList = async () => {
  if (!baseRef.value) return
  baseRef.value.setLoading(true)
  try {
    const searchForm = baseRef.value.searchForm as KnowledgeCategoryListParams
    const pagination = baseRef.value.pagination
    const params: KnowledgeCategoryListParams = {
      current: pagination.current,
      size: pagination.size,
      categoryName: searchForm.categoryName as string | undefined,
      categoryCode: searchForm.categoryCode as string | undefined,
    }
    const result = await getCategoryListApi(params)
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

const handleAdd = () => {
  if (!baseRef.value) return
  baseRef.value.formData.parentId = 0
  baseRef.value.formData.sort = 0
}

const handleEdit = () => {}

const onDelete = async (row: Record<string, unknown>) => {
  const r = row as unknown as KnowledgeCategory
  try {
    await ElMessageBox.confirm(`确定要删除分类「${r.categoryName || r.id}」吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await deleteCategoryApi(r.id)
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
      const data: KnowledgeCategoryUpdateParams = {
        categoryName: payload.categoryName as string,
        parentId: payload.parentId as number,
        sort: payload.sort as number,
        icon: payload.icon as string,
        description: payload.description as string,
      }
      await updateCategoryApi(id, data)
      ElMessage.success('更新成功')
    } else {
      const data: KnowledgeCategoryCreateParams = {
        categoryCode: (payload.categoryCode as string) || '',
        categoryName: (payload.categoryName as string) || '',
        parentId: (payload.parentId as number) ?? 0,
        sort: (payload.sort as number) ?? 0,
        icon: payload.icon as string,
        description: payload.description as string,
      }
      await createCategoryApi(data)
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
    baseRef.value.searchForm = { categoryName: '', categoryCode: '' }
  }
  loadList()
})
</script>
