<template>
  <BaseManageView
    ref="baseRef"
    title="角色管理"
    :show-add-button="true"
    :show-selection="false"
    add-permission="system:role:add"
    edit-permission="system:role:edit"
    delete-permission="system:role:delete"
    @search="handleSearch"
    @add="handleAdd"
    @edit="(row) => handleEdit(row as unknown as Role)"
    @delete="(row) => handleDelete(row as unknown as Role)"
    @submit="handleSubmit"
  >
    <!-- 自定义搜索表单 -->
    <template #search-form="{ searchForm }">
      <el-form-item label="角色编码">
        <el-input v-model="searchForm.roleCode" placeholder="请输入角色编码" clearable />
      </el-form-item>
      <el-form-item label="角色名称">
        <el-input v-model="searchForm.roleName" placeholder="请输入角色名称" clearable />
      </el-form-item>
    </template>

    <!-- 自定义表格列 -->
    <template #table-columns>
      <el-table-column prop="roleCode" label="角色编码" width="150" />
      <el-table-column prop="roleName" label="角色名称" width="150" />
      <el-table-column prop="roleDesc" label="角色描述" />
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column prop="updateTime" label="更新时间" width="180" />
    </template>

    <!-- 自定义操作按钮 -->
    <template #actions="{ row }">
      <el-button 
        v-permission="'system:role:edit'" 
        type="primary" 
        link 
        size="small" 
        @click="handleEdit(row as Role)"
      >
        编辑
      </el-button>
      <el-button 
        v-permission="'system:role:assign'" 
        type="warning" 
        link 
        size="small" 
        @click="handleAssignPermission(row as Role)"
      >
        分配权限
      </el-button>
      <el-button 
        v-permission="'system:role:delete'" 
        type="danger" 
        link 
        size="small" 
        @click="handleDelete(row as Role)"
      >
        删除
      </el-button>
    </template>

    <!-- 自定义对话框表单 -->
    <template #dialog-form="{ formData }">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="formData.roleCode" :disabled="isEdit" placeholder="请输入角色编码" />
        </el-form-item>
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="formData.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色描述" prop="roleDesc">
          <el-input
            v-model="formData.roleDesc"
            type="textarea"
            :rows="3"
            placeholder="请输入角色描述"
          />
        </el-form-item>
      </el-form>
    </template>
  </BaseManageView>

  <!-- 分配权限对话框 -->
  <el-dialog
    v-model="permissionDialogVisible"
    title="分配权限"
    width="600px"
    @close="handlePermissionDialogClose"
  >
    <el-tree
      ref="permissionTreeRef"
      :data="permissionTree"
      :props="{ label: 'permissionName', children: 'children' }"
      show-checkbox
      node-key="id"
      :default-checked-keys="checkedPermissionIds"
      check-strictly
    />
    <template #footer>
      <el-button @click="permissionDialogVisible = false" :disabled="assignPermissionLoading">取消</el-button>
      <el-button type="primary" @click="handleAssignPermissionSubmit" :loading="assignPermissionLoading">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type ElTree } from 'element-plus'
import BaseManageView from '../../../components/admin/BaseManageView.vue'
import {
  getRoleListApi,
  createRoleApi,
  updateRoleApi,
  deleteRoleApi,
  getRolePermissionsApi,
  type Role,
  type RoleListParams,
  type CreateRoleParams,
  type UpdateRoleParams,
} from '../../../api/admin/role'
import { getPermissionTreeApi, type PermissionTreeNode } from '../../../api/admin/permission'
import { useAuthStore } from '../../../stores/auth'

const baseRef = ref<InstanceType<typeof BaseManageView>>()
const formRef = ref<FormInstance>()
const permissionTreeRef = ref<InstanceType<typeof ElTree>>()
const permissionTree = ref<PermissionTreeNode[]>([])
const checkedPermissionIds = ref<number[]>([])
const permissionDialogVisible = ref(false)
const currentRoleId = ref<number>(0)
const isEdit = ref(false)
const assignPermissionLoading = ref(false)
const authStore = useAuthStore()

const formRules = {
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
}

// 加载角色列表
const loadRoleList = async () => {
  if (!baseRef.value) return

  baseRef.value.setLoading(true)
  try {
    const searchForm = baseRef.value.searchForm as RoleListParams
    const pagination = baseRef.value.pagination

    const params: RoleListParams = {
      current: pagination.current,
      size: pagination.size,
      ...searchForm,
    }
    const result = await getRoleListApi(params)
    baseRef.value.setDataList(result.records as unknown as Record<string, unknown>[])
    baseRef.value.setPagination({
      current: result.current,
      size: result.size,
      total: result.total,
    })
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : '加载角色列表失败'
    ElMessage.error(message)
  } finally {
    baseRef.value.setLoading(false)
  }
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

// 搜索
const handleSearch = () => {
  loadRoleList()
}

// 新增
const handleAdd = () => {
  if (!baseRef.value) return
  isEdit.value = false
  baseRef.value.dialogVisible = true
  baseRef.value.formData = {
    roleCode: '',
    roleName: '',
    roleDesc: '',
  }
}

// 编辑
const handleEdit = (row: Role) => {
  if (!baseRef.value) return
  isEdit.value = true
  baseRef.value.dialogVisible = true
  baseRef.value.formData = {
    id: row.id,
    roleCode: row.roleCode,
    roleName: row.roleName,
    roleDesc: row.roleDesc || '',
  }
}

// 删除
const handleDelete = async (row: Role) => {
  try {
    await ElMessageBox.confirm(`确定要删除角色 "${row.roleName}" 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await deleteRoleApi(row.id)
    ElMessage.success('删除成功')
    loadRoleList()
  } catch (error: unknown) {
    if (error !== 'cancel') {
      const message = error instanceof Error ? error.message : '删除失败'
      ElMessage.error(message)
    }
  }
}

// 分配权限
const handleAssignPermission = async (row: Role) => {
  currentRoleId.value = row.id
  permissionDialogVisible.value = true

  // 加载角色已有权限
  try {
    const permissionIds = await getRolePermissionsApi(row.id)
    checkedPermissionIds.value = permissionIds
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : '加载角色权限失败'
    ElMessage.error(message)
    checkedPermissionIds.value = []
  }
}

// 分配权限提交
const handleAssignPermissionSubmit = async () => {
  if (!permissionTreeRef.value) return

  const checkedKeys = permissionTreeRef.value.getCheckedKeys() as number[]
  const halfCheckedKeys = permissionTreeRef.value.getHalfCheckedKeys() as number[]
  const allCheckedKeys = [...checkedKeys, ...halfCheckedKeys]

  assignPermissionLoading.value = true
  try {
    const updateParams: UpdateRoleParams = {
      permissionIds: allCheckedKeys,
    }
    await updateRoleApi(currentRoleId.value, updateParams)
    ElMessage.success('分配权限成功')
    permissionDialogVisible.value = false
    // 重置权限对话框状态
    handlePermissionDialogClose()
    // 刷新用户权限
    try {
      await authStore.refreshPermissions()
      // 触发菜单权限刷新事件，以便导航栏实时更新
      window.dispatchEvent(new CustomEvent('permission-refresh'))
    } catch (error) {
      console.error('刷新权限失败:', error)
    }
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : '分配权限失败'
    ElMessage.error(message)
  } finally {
    assignPermissionLoading.value = false
  }
}

// 提交表单
const handleSubmit = async (formData: Record<string, unknown>) => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEdit.value) {
          const updateParams: UpdateRoleParams = {
            roleCode: formData.roleCode as string,
            roleName: formData.roleName as string,
            roleDesc: formData.roleDesc as string,
          }
          await updateRoleApi(formData.id as number, updateParams)
          ElMessage.success('更新成功')
        } else {
          const createParams: CreateRoleParams = {
            roleCode: formData.roleCode as string,
            roleName: formData.roleName as string,
            roleDesc: formData.roleDesc as string,
          }
          await createRoleApi(createParams)
          ElMessage.success('新增成功')
        }
        if (baseRef.value) {
          baseRef.value.dialogVisible = false
        }
        loadRoleList()
      } catch (error: unknown) {
        const message = error instanceof Error ? error.message : '操作失败'
        ElMessage.error(message)
      }
    }
  })
}

// 权限对话框关闭
const handlePermissionDialogClose = () => {
  checkedPermissionIds.value = []
  currentRoleId.value = 0
}

onMounted(() => {
  // 初始化搜索表单
  if (baseRef.value) {
    baseRef.value.searchForm = {
      roleCode: '',
      roleName: '',
    }
  }
  loadRoleList()
  loadPermissionTree()
})
</script>

<style scoped></style>
