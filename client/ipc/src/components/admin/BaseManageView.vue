<template>
  <div class="base-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ title }}</span>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <slot name="search-form" :searchForm="searchForm">
          <!-- 默认搜索表单内容 -->
        </slot>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button 
            v-permission="addPermission" 
            type="primary" 
            @click="handleAdd" 
            v-if="showAddButton"
          >
            新增
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table
        :data="dataList"
        v-loading="loading"
        border
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" v-if="showSelection" />
        <slot name="table-columns" :dataList="dataList">
          <!-- 默认表格列 -->
          <el-table-column type="index" label="序号" width="60" />
        </slot>
        <el-table-column label="操作" width="200" fixed="right" v-if="showActions">
          <template #default="{ row }">
            <slot name="actions" :row="row">
              <el-button 
                v-permission="editPermission" 
                type="primary" 
                link 
                size="small" 
                @click="handleEdit(row)"
              >
                编辑
              </el-button>
              <el-button 
                v-permission="deletePermission" 
                type="danger" 
                link 
                size="small" 
                @click="handleDelete(row)"
              >
                删除
              </el-button>
            </slot>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <AdminPagination
        v-if="showPagination"
        :current="pagination.current"
        :size="pagination.size"
        :total="pagination.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <slot name="dialog-form" :formData="formData" :dialogVisible="dialogVisible">
        <!-- 对话框表单内容 -->
      </slot>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessageBox } from 'element-plus'
import AdminPagination from './AdminPagination.vue'

interface Props {
  title: string
  showAddButton?: boolean
  showActions?: boolean
  showSelection?: boolean
  showPagination?: boolean
  addPermission?: string
  editPermission?: string
  deletePermission?: string
}

withDefaults(defineProps<Props>(), {
  showAddButton: true,
  showActions: true,
  showSelection: false,
  showPagination: true,
})

const emit = defineEmits<{
  (e: 'search', searchForm: Record<string, unknown>): void
  (e: 'add'): void
  (e: 'edit', row: Record<string, unknown>): void
  (e: 'delete', row: Record<string, unknown>): void
  (e: 'submit', formData: Record<string, unknown>): void
  (e: 'selection-change', selection: Record<string, unknown>[]): void
}>()

const loading = ref(false)
const dataList = ref<Record<string, unknown>[]>([])
const searchForm = reactive<Record<string, unknown>>({})
const formData = reactive<Record<string, unknown>>({})
const dialogVisible = ref(false)
const dialogTitle = ref('新增')
const isEdit = ref(false)
const selectedRows = ref<Record<string, unknown>[]>([])

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0,
})

const handleSearch = () => {
  pagination.current = 1
  emit('search', searchForm)
}

const handleReset = () => {
  Object.keys(searchForm).forEach((key) => {
    searchForm[key] = undefined
  })
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增'
  isEdit.value = false
  Object.keys(formData).forEach((key) => {
    formData[key] = undefined
  })
  dialogVisible.value = true
  emit('add')
}

const handleEdit = (row: Record<string, unknown>) => {
  dialogTitle.value = '编辑'
  isEdit.value = true
  Object.assign(formData, row)
  dialogVisible.value = true
  emit('edit', row)
}

const handleDelete = async (row: Record<string, unknown>) => {
  try {
    await ElMessageBox.confirm('确定要删除这条记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    emit('delete', row)
  } catch {
    // 用户取消
  }
}

const handleSubmit = () => {
  emit('submit', { ...formData, isEdit: isEdit.value })
}

const handleDialogClose = () => {
  Object.keys(formData).forEach((key) => {
    formData[key] = undefined
  })
}

const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.current = 1 // 改变每页数量时重置到第一页
  emit('search', searchForm)
}

const handleCurrentChange = (current: number) => {
  pagination.current = current
  emit('search', searchForm) // 直接触发搜索，不重置页码
}

const handleSelectionChange = (selection: Record<string, unknown>[]) => {
  selectedRows.value = selection
  emit('selection-change', selection)
}

defineExpose({
  loading,
  dataList,
  pagination,
  dialogVisible,
  searchForm,
  formData,
  selectedRows,
  setLoading: (value: boolean) => {
    loading.value = value
  },
  setDataList: (data: Record<string, unknown>[]) => {
    dataList.value = data
  },
  setPagination: (paginationData: { current: number; size: number; total: number }) => {
    pagination.current = paginationData.current
    pagination.size = paginationData.size
    pagination.total = paginationData.total
  },
  handleEdit,
})
</script>

<style scoped>
.base-manage {
  padding: 0;
}

.search-form {
  margin-bottom: 20px;
}

.card-header {
  font-size: 16px;
  font-weight: 600;
}
</style>
