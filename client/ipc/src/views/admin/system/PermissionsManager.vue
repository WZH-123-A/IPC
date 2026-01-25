<template>
  <div class="permission-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>权限管理</span>
          <el-button v-permission="'system:permission:add'" type="primary" @click="handleAdd">新增权限</el-button>
        </div>
      </template>

      <!-- 权限树 -->
      <el-tree
        :data="permissionTree"
        :props="{ label: 'permissionName', children: 'children' }"
        default-expand-all
        node-key="id"
      >
        <template #default="{ data }">
          <div class="tree-node">
            <span class="node-label">{{ data.permissionName }}</span>
            <span class="node-code">({{ data.permissionCode }})</span>
            <el-tag :type="getPermissionTypeTag(data.permissionType)" size="small" class="node-tag">
              {{ getPermissionTypeName(data.permissionType) }}
            </el-tag>
            <div class="node-actions">
              <el-button 
                v-permission="'system:permission:add'" 
                type="primary" 
                link 
                size="small" 
                @click="handleAddChild(data)"
              >
                添加子权限
              </el-button>
              <el-button 
                v-permission="'system:permission:edit'" 
                type="primary" 
                link 
                size="small" 
                @click="handleEdit(data)"
              >
                编辑
              </el-button>
              <el-button 
                v-permission="'system:permission:delete'" 
                type="danger" 
                link 
                size="small" 
                @click="handleDelete(data)"
              >
                删除
              </el-button>
            </div>
          </div>
        </template>
      </el-tree>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="权限编码" prop="permissionCode">
          <el-input
            v-model="formData.permissionCode"
            :disabled="isEdit"
            placeholder="请输入权限编码"
          />
        </el-form-item>
        <el-form-item label="权限名称" prop="permissionName">
          <el-input v-model="formData.permissionName" placeholder="请输入权限名称" />
        </el-form-item>
        <el-form-item label="权限类型" prop="permissionType">
          <el-select
            v-model="formData.permissionType"
            placeholder="请选择权限类型"
            style="width: 100%"
          >
            <el-option label="菜单权限" :value="1" />
            <el-option label="按钮权限" :value="2" />
            <el-option label="接口权限" :value="3" />
            <el-option label="路由权限" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="父权限" prop="parentId">
          <el-tree-select
            v-model="formData.parentId"
            :data="permissionTree"
            :props="{ label: 'permissionName', children: 'children', value: 'id' }"
            placeholder="请选择父权限（不选则为顶级）"
            check-strictly
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number
            v-model="formData.sort"
            :min="0"
            placeholder="请输入排序"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import {
  getPermissionTreeApi,
  createPermissionApi,
  updatePermissionApi,
  deletePermissionApi,
  type PermissionTreeNode,
  type CreatePermissionParams,
  type UpdatePermissionParams,
} from '../../../api/admin/permission'

const permissionTree = ref<PermissionTreeNode[]>([])

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const formData = reactive<CreatePermissionParams & { id?: number }>({
  permissionCode: '',
  permissionName: '',
  permissionType: 1,
  parentId: 0,
  sort: 0,
})

const formRules = {
  permissionCode: [{ required: true, message: '请输入权限编码', trigger: 'blur' }],
  permissionName: [{ required: true, message: '请输入权限名称', trigger: 'blur' }],
  permissionType: [{ required: true, message: '请选择权限类型', trigger: 'change' }],
}

const dialogTitle = computed(() => (isEdit.value ? '编辑权限' : '新增权限'))

// 获取权限类型名称
const getPermissionTypeName = (type: number) => {
  const typeMap: Record<number, string> = {
    1: '菜单',
    2: '按钮',
    3: '接口',
    4: '路由',
  }
  return typeMap[type] || '未知'
}

// 获取权限类型标签类型
const getPermissionTypeTag = (type: number) => {
  const tagMap: Record<number, string> = {
    1: 'success',
    2: 'warning',
    3: 'info',
    4: 'primary',
  }
  return tagMap[type] || ''
}

// 加载权限树
const loadPermissionTree = async () => {
  try {
    permissionTree.value = await getPermissionTreeApi()
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : '加载权限树失败'
    ElMessage.error(message)
  }
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
  resetForm()
}

// 添加子权限
const handleAddChild = (parent: PermissionTreeNode) => {
  isEdit.value = false
  dialogVisible.value = true
  resetForm()
  formData.parentId = parent.id
}

// 编辑
const handleEdit = (data: PermissionTreeNode) => {
  isEdit.value = true
  dialogVisible.value = true
  formData.id = data.id
  formData.permissionCode = data.permissionCode
  formData.permissionName = data.permissionName
  formData.permissionType = data.permissionType
  formData.parentId = data.parentId
  formData.sort = data.sort
}

// 删除
const handleDelete = async (data: PermissionTreeNode) => {
  try {
    await ElMessageBox.confirm(`确定要删除权限 "${data.permissionName}" 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await deletePermissionApi(data.id)
    ElMessage.success('删除成功')
    loadPermissionTree()
  } catch (error: unknown) {
    if (error !== 'cancel') {
      const message = error instanceof Error ? error.message : '删除失败'
      ElMessage.error(message)
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEdit.value) {
          const updateParams: UpdatePermissionParams = {
            permissionName: formData.permissionName,
            permissionType: formData.permissionType,
            parentId: formData.parentId,
            sort: formData.sort,
          }
          await updatePermissionApi(formData.id!, updateParams)
          ElMessage.success('更新成功')
        } else {
          const createParams: CreatePermissionParams = {
            permissionCode: formData.permissionCode,
            permissionName: formData.permissionName,
            permissionType: formData.permissionType,
            parentId: formData.parentId || 0,
            sort: formData.sort || 0,
          }
          await createPermissionApi(createParams)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        loadPermissionTree()
      } catch (error: unknown) {
        const message = error instanceof Error ? error.message : '操作失败'
        ElMessage.error(message)
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  formData.id = undefined
  formData.permissionCode = ''
  formData.permissionName = ''
  formData.permissionType = 1
  formData.parentId = 0
  formData.sort = 0
  formRef.value?.resetFields()
}

// 对话框关闭
const handleDialogClose = () => {
  resetForm()
}

onMounted(() => {
  loadPermissionTree()
})
</script>

<style scoped>
.permission-manage {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tree-node {
  display: flex;
  align-items: center;
  flex: 1;
  padding-right: 8px;
}

.node-label {
  font-weight: 500;
  margin-right: 8px;
}

.node-code {
  color: #909399;
  font-size: 12px;
  margin-right: 8px;
}

.node-tag {
  margin-right: 8px;
}

.node-actions {
  margin-left: auto;
}
</style>
