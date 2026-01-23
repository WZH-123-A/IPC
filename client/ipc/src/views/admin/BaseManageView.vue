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
          <el-button type="primary" @click="handleAdd" v-if="showAddButton">新增</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="dataList" v-loading="loading" border stripe>
        <slot name="table-columns" :dataList="dataList">
          <!-- 默认表格列 -->
          <el-table-column type="index" label="序号" width="60" />
        </slot>
        <el-table-column label="操作" width="200" fixed="right" v-if="showActions">
          <template #default="{ row }">
            <slot name="actions" :row="row">
              <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
              <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
            </slot>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

interface Props {
  title: string
  showAddButton?: boolean
  showActions?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  showAddButton: true,
  showActions: true,
})

const emit = defineEmits<{
  search: [searchForm: any]
  add: []
  edit: [row: any]
  delete: [row: any]
  submit: [formData: any]
}>()

const loading = ref(false)
const dataList = ref<any[]>([])
const searchForm = reactive<any>({})
const formData = reactive<any>({})
const dialogVisible = ref(false)
const dialogTitle = ref('新增')
const isEdit = ref(false)

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

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑'
  isEdit.value = true
  Object.assign(formData, row)
  dialogVisible.value = true
  emit('edit', row)
}

const handleDelete = async (row: any) => {
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
  handleSearch()
}

const handleCurrentChange = (current: number) => {
  pagination.current = current
  handleSearch()
}

defineExpose({
  loading,
  dataList,
  pagination,
  dialogVisible,
})
</script>

<style scoped>
.base-manage {
  padding: 0;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.card-header {
  font-size: 16px;
  font-weight: 600;
}
</style>

