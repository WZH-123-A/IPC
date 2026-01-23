<template>
  <div class="role-manage">
    <el-card>
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="角色编码">
          <el-input v-model="searchForm.roleCode" placeholder="请输入角色编码" clearable />
        </el-form-item>
        <el-form-item label="角色名称">
          <el-input v-model="searchForm.roleName" placeholder="请输入角色名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="primary" @click="handleAdd">新增</el-button>
        </el-form-item>
      </el-form>

      <!-- 角色表格 -->
      <el-table :data="roleList" v-loading="loading" border stripe>
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="roleCode" label="角色编码" width="150" />
        <el-table-column prop="roleName" label="角色名称" width="150" />
        <el-table-column prop="roleDesc" label="角色描述" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link size="small" @click="handleAssignPermission(row)">分配权限</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
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
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

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
        <el-button @click="permissionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAssignPermissionSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type ElTree } from 'element-plus'
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
} from '../../api/admin'
import { getPermissionTreeApi, type PermissionTreeNode } from '../../api/admin'

const loading = ref(false)
const roleList = ref<Role[]>([])
const permissionTree = ref<PermissionTreeNode[]>([])
const checkedPermissionIds = ref<number[]>([])

const searchForm = reactive<RoleListParams>({
  roleCode: '',
  roleName: '',
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0,
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const formData = reactive<CreateRoleParams & { id?: number }>({
  roleCode: '',
  roleName: '',
  roleDesc: '',
})

const formRules = {
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
}

const permissionDialogVisible = ref(false)
const permissionTreeRef = ref<InstanceType<typeof ElTree>>()
const currentRoleId = ref<number>(0)

const dialogTitle = computed(() => (isEdit.value ? '编辑角色' : '新增角色'))

// 加载角色列表
const loadRoleList = async () => {
  loading.value = true
  try {
    const params: RoleListParams = {
      current: pagination.current,
      size: pagination.size,
      ...searchForm,
    }
    const result = await getRoleListApi(params)
    roleList.value = result.records
    pagination.total = result.total
  } catch (error: any) {
    ElMessage.error(error.message || '加载角色列表失败')
  } finally {
    loading.value = false
  }
}

// 加载权限树
const loadPermissionTree = async () => {
  try {
    permissionTree.value = await getPermissionTreeApi()
  } catch (error: any) {
    ElMessage.error(error.message || '加载权限树失败')
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadRoleList()
}

// 重置
const handleReset = () => {
  searchForm.roleCode = ''
  searchForm.roleName = ''
  handleSearch()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
  resetForm()
}

// 编辑
const handleEdit = (row: Role) => {
  isEdit.value = true
  dialogVisible.value = true
  formData.id = row.id
  formData.roleCode = row.roleCode
  formData.roleName = row.roleName
  formData.roleDesc = row.roleDesc || ''
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
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
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
  } catch (error: any) {
    ElMessage.error(error.message || '加载角色权限失败')
    checkedPermissionIds.value = []
  }
}

// 分配权限提交
const handleAssignPermissionSubmit = async () => {
  if (!permissionTreeRef.value) return
  
  const checkedKeys = permissionTreeRef.value.getCheckedKeys() as number[]
  const halfCheckedKeys = permissionTreeRef.value.getHalfCheckedKeys() as number[]
  const allCheckedKeys = [...checkedKeys, ...halfCheckedKeys]
  
  try {
    const updateParams: UpdateRoleParams = {
      permissionIds: allCheckedKeys,
    }
    await updateRoleApi(currentRoleId.value, updateParams)
    ElMessage.success('分配权限成功')
    permissionDialogVisible.value = false
  } catch (error: any) {
    ElMessage.error(error.message || '分配权限失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEdit.value) {
          const updateParams: UpdateRoleParams = {
            roleCode: formData.roleCode,
            roleName: formData.roleName,
            roleDesc: formData.roleDesc,
          }
          await updateRoleApi(formData.id!, updateParams)
          ElMessage.success('更新成功')
        } else {
          const createParams: CreateRoleParams = {
            roleCode: formData.roleCode,
            roleName: formData.roleName,
            roleDesc: formData.roleDesc,
          }
          await createRoleApi(createParams)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        loadRoleList()
      } catch (error: any) {
        ElMessage.error(error.message || '操作失败')
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  formData.id = undefined
  formData.roleCode = ''
  formData.roleName = ''
  formData.roleDesc = ''
  formRef.value?.resetFields()
}

// 对话框关闭
const handleDialogClose = () => {
  resetForm()
}

// 权限对话框关闭
const handlePermissionDialogClose = () => {
  checkedPermissionIds.value = []
  currentRoleId.value = 0
}

// 分页大小改变
const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.current = 1
  loadRoleList()
}

// 当前页改变
const handleCurrentChange = (current: number) => {
  pagination.current = current
  loadRoleList()
}

onMounted(() => {
  loadRoleList()
  loadPermissionTree()
})
</script>

<style scoped>
.role-manage {
  height: 100%;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
