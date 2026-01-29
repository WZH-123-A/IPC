<template>
  <BaseManageView
    ref="baseRef"
    title="知识库内容管理"
    add-permission="knowledge:content:add"
    edit-permission="knowledge:content:edit"
    delete-permission="knowledge:content:delete"
    @search="handleSearch"
    @add="handleAdd"
    @edit="(row) => handleEdit(row as unknown as KnowledgeContent)"
    @delete="onDelete"
    @submit="handleSubmit"
  >
    <template #search-form="{ searchForm }">
      <el-form-item label="分类">
        <el-select
          v-model="searchForm.categoryId"
          placeholder="全部分类"
          clearable
          style="width: 160px"
        >
          <el-option
            v-for="c in categoryOptions"
            :key="c.id"
            :label="c.categoryName"
            :value="c.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="标题">
        <el-input
          v-model="searchForm.title"
          placeholder="请输入标题"
          clearable
          style="width: 180px"
        />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 120px">
          <el-option label="草稿" :value="0" />
          <el-option label="已发布" :value="1" />
          <el-option label="已下架" :value="2" />
        </el-select>
      </el-form-item>
    </template>

    <template #table-columns>
      <el-table-column label="封面" width="90" align="center">
        <template #default="{ row }">
          <img
            v-if="(row as KnowledgeContent).coverImage"
            :src="resolveCoverUrl((row as KnowledgeContent).coverImage)"
            alt="封面"
            class="cover-thumb"
          />
          <span v-else class="text-gray">-</span>
        </template>
      </el-table-column>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
      <el-table-column prop="categoryName" label="分类" width="110" />
      <el-table-column prop="contentType" label="类型" width="80">
        <template #default="{ row }">
          {{ contentTypeLabel((row as KnowledgeContent).contentType) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          {{ statusLabel((row as KnowledgeContent).status) }}
        </template>
      </el-table-column>
      <el-table-column label="标签" min-width="140" show-overflow-tooltip>
        <template #default="{ row }">
          {{ getTagNames((row as KnowledgeContent).tagIds) }}
        </template>
      </el-table-column>
      <el-table-column prop="viewCount" label="浏览" width="70" />
      <el-table-column prop="createTime" label="创建时间" width="180" />
    </template>

    <template #dialog-form="{ formData, dialogVisible }">
      <el-form v-if="dialogVisible" :model="formData" label-width="100px" class="content-form">
        <el-form-item label="分类" required>
          <el-select v-model="formData.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="c in categoryOptions"
              :key="c.id"
              :label="c.categoryName"
              :value="c.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="标题" required>
          <el-input v-model="formData.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="副标题">
          <el-input v-model="formData.subtitle" placeholder="选填" />
        </el-form-item>
        <el-form-item label="内容类型" required>
          <el-select v-model="formData.contentType" placeholder="请选择" style="width: 100%">
            <el-option label="图文" :value="1" />
            <el-option label="视频" :value="2" />
            <el-option label="音频" :value="3" />
            <el-option label="文档" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="封面图">
          <div class="cover-upload">
            <img
              v-if="formData.coverImage"
              :src="resolveCoverUrl(formData.coverImage as string)"
              alt="封面"
              class="cover-preview"
            />
            <div class="cover-actions">
              <el-button type="primary" :loading="uploading" @click="triggerCoverUpload">
                {{ formData.coverImage ? '更换封面' : '上传封面' }}
              </el-button>
              <el-button
                v-if="formData.coverImage"
                type="danger"
                plain
                @click="removeCover"
              >
                删除封面
              </el-button>
            </div>
            <input
              ref="coverInputRef"
              type="file"
              accept="image/*"
              class="hidden-input"
              @change="onCoverFileChange"
            />
          </div>
        </el-form-item>
        <el-form-item label="正文内容">
          <el-input
            v-model="formData.content"
            type="textarea"
            :rows="8"
            placeholder="支持 HTML，可直接粘贴富文本"
          />
        </el-form-item>
        <el-form-item label="视频地址">
          <el-input v-model="formData.videoUrl" placeholder="选填" />
        </el-form-item>
        <el-form-item label="音频地址">
          <el-input v-model="formData.audioUrl" placeholder="选填" />
        </el-form-item>
        <el-form-item label="文档地址">
          <el-input v-model="formData.documentUrl" placeholder="选填" />
        </el-form-item>
        <el-form-item label="来源">
          <el-input v-model="formData.source" placeholder="选填" />
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="formData.author" placeholder="选填" />
        </el-form-item>
        <el-form-item label="标签">
          <el-select
            v-model="formData.tagIds"
            multiple
            collapse-tags
            collapse-tags-tooltip
            placeholder="请选择标签（可多选）"
            style="width: 100%"
            value-key="id"
          >
            <el-option
              v-for="t in tagOptions"
              :key="t.id"
              :label="t.tagName"
              :value="t.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="formData.status" style="width: 120px">
            <el-option label="草稿" :value="0" />
            <el-option label="已发布" :value="1" />
            <el-option label="已下架" :value="2" />
          </el-select>
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
  getTagListApi,
  getContentListApi,
  getContentByIdApi,
  createContentApi,
  updateContentApi,
  deleteContentApi,
  uploadKnowledgeImageApi,
  type KnowledgeCategory,
  type KnowledgeTag,
  type KnowledgeContent,
  type KnowledgeContentListParams,
  type KnowledgeContentCreateParams,
  type KnowledgeContentUpdateParams,
} from '../../../api/admin/knowledge'

const baseRef = ref<InstanceType<typeof BaseManageView>>()
const categoryOptions = ref<KnowledgeCategory[]>([])
const tagOptions = ref<KnowledgeTag[]>([])
const uploading = ref(false)
const coverInputRef = ref<HTMLInputElement | null>(null)

function resolveCoverUrl(url?: string): string {
  if (!url) return ''
  if (url.startsWith('http')) return url
  const origin = import.meta.env.DEV ? 'http://localhost:8080' : window.location.origin
  return `${origin}${url}`
}

function contentTypeLabel(t?: number): string {
  const m: Record<number, string> = { 1: '图文', 2: '视频', 3: '音频', 4: '文档' }
  return t != null ? (m[t] ?? '-') : '-'
}

function statusLabel(s?: number): string {
  const m: Record<number, string> = { 0: '草稿', 1: '已发布', 2: '已下架' }
  return s != null ? (m[s] ?? '-') : '-'
}

function getTagNames(ids?: number[]): string {
  if (!ids?.length) return '-'
  return ids
    .map((id) => tagOptions.value.find((t) => t.id === id)?.tagName)
    .filter(Boolean)
    .join('，') || '-'
}

const loadCategories = async () => {
  try {
    const res = await getCategoryListApi({ current: 1, size: 500 })
    categoryOptions.value = res.records
  } catch {
    categoryOptions.value = []
  }
}

const loadTags = async () => {
  try {
    const res = await getTagListApi({ current: 1, size: 500 })
    tagOptions.value = res.records
  } catch {
    tagOptions.value = []
  }
}

const loadList = async () => {
  if (!baseRef.value) return
  baseRef.value.setLoading(true)
  try {
    const searchForm = baseRef.value.searchForm as KnowledgeContentListParams
    const pagination = baseRef.value.pagination
    const params: KnowledgeContentListParams = {
      current: pagination.current,
      size: pagination.size,
      categoryId: searchForm.categoryId as number | undefined,
      title: searchForm.title as string | undefined,
      status: searchForm.status as number | undefined,
    }
    const result = await getContentListApi(params)
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
  baseRef.value.formData.contentType = 1
  baseRef.value.formData.status = 1
  baseRef.value.formData.tagIds = []
}

const handleEdit = async (row: KnowledgeContent) => {
  if (!baseRef.value) return
  try {
    const detail = await getContentByIdApi(row.id)
    Object.assign(baseRef.value.formData, detail)
    baseRef.value.formData.tagIds = Array.isArray(detail.tagIds) ? [...detail.tagIds] : []
  } catch (error: unknown) {
    ElMessage.error(error instanceof Error ? error.message : '获取内容详情失败')
  }
}

const onDelete = async (row: Record<string, unknown>) => {
  const r = row as unknown as KnowledgeContent
  try {
    await ElMessageBox.confirm(`确定要删除内容「${r.title || r.id}」吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await deleteContentApi(r.id)
    ElMessage.success('删除成功')
    loadList()
  } catch (error: unknown) {
    if (error !== 'cancel') {
      ElMessage.error(error instanceof Error ? error.message : '删除失败')
    }
  }
}

const triggerCoverUpload = () => {
  coverInputRef.value?.click()
}

const removeCover = () => {
  if (baseRef.value) {
    baseRef.value.formData.coverImage = ''
    ElMessage.success('已清除封面')
  }
}

const onCoverFileChange = async (e: Event) => {
  const input = e.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file || !baseRef.value) return
  if (!file.type.startsWith('image/')) {
    ElMessage.warning('请选择图片文件')
    input.value = ''
    return
  }
  uploading.value = true
  try {
    const res = await uploadKnowledgeImageApi(file)
    if (res.fileUrl) {
      baseRef.value.formData.coverImage = res.fileUrl
      ElMessage.success('封面上传成功')
    }
  } catch (err: unknown) {
    ElMessage.error(err instanceof Error ? err.message : '封面上传失败')
  } finally {
    uploading.value = false
    input.value = ''
  }
}

const handleSubmit = async (payload: Record<string, unknown> & { isEdit?: boolean }) => {
  if (!baseRef.value) return
  const { isEdit } = payload
  const id = payload.id as number | undefined
  try {
    if (isEdit && id != null) {
      const data: KnowledgeContentUpdateParams = {
        categoryId: payload.categoryId as number,
        title: payload.title as string,
        subtitle: payload.subtitle as string,
        contentType: payload.contentType as number,
        coverImage: payload.coverImage as string,
        content: payload.content as string,
        videoUrl: payload.videoUrl as string,
        audioUrl: payload.audioUrl as string,
        documentUrl: payload.documentUrl as string,
        source: payload.source as string,
        author: payload.author as string,
        status: payload.status as number,
        tagIds: Array.isArray(payload.tagIds) ? payload.tagIds : [],
      }
      await updateContentApi(id, data)
      ElMessage.success('更新成功')
    } else {
      const data: KnowledgeContentCreateParams = {
        categoryId: (payload.categoryId as number) ?? 0,
        title: (payload.title as string) || '',
        subtitle: payload.subtitle as string,
        contentType: (payload.contentType as number) ?? 1,
        coverImage: payload.coverImage as string,
        content: payload.content as string,
        videoUrl: payload.videoUrl as string,
        audioUrl: payload.audioUrl as string,
        documentUrl: payload.documentUrl as string,
        source: payload.source as string,
        author: payload.author as string,
        status: (payload.status as number) ?? 1,
        tagIds: Array.isArray(payload.tagIds) ? payload.tagIds : [],
      }
      await createContentApi(data)
      ElMessage.success('新增成功')
    }
    baseRef.value.dialogVisible = false
    loadList()
  } catch (error: unknown) {
    ElMessage.error(error instanceof Error ? error.message : '保存失败')
  }
}

onMounted(async () => {
  await Promise.all([loadCategories(), loadTags()])
  if (baseRef.value) {
    baseRef.value.searchForm = { categoryId: undefined, title: '', status: undefined }
  }
  loadList()
})
</script>

<style scoped>
.content-form {
  max-width: 560px;
}
.cover-upload {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}
.cover-preview {
  width: 120px;
  height: 80px;
  object-fit: cover;
  border-radius: 6px;
  border: 1px solid var(--el-border-color);
  display: block;
}
.cover-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}
.hidden-input {
  position: absolute;
  width: 0;
  height: 0;
  opacity: 0;
}
.cover-thumb {
  width: 56px;
  height: 40px;
  object-fit: cover;
  border-radius: 4px;
  vertical-align: middle;
}
.text-gray {
  color: var(--el-text-color-secondary);
}
</style>
