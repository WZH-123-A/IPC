<template>
  <div class="user-manage">
    <el-card>
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="searchForm.realName" placeholder="请输入真实姓名" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="primary" @click="handleAdd">新增</el-button>
        </el-form-item>
      </el-form>

      <!-- 用户表格 -->
      <el-table :data="userList" v-loading="loading" border stripe>
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="{ row }">
            <span>{{ row.gender === 1 ? '男' : row.gender === 2 ? '女' : '未知' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginTime" label="最后登录时间" width="180" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link size="small" @click="handleResetPassword(row)">重置密码</el-button>
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
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" :disabled="isEdit" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input v-model="formData.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="formData.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="formData.gender">
            <el-radio :label="0">未知</el-radio>
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="角色" prop="roleIds">
          <el-select v-model="formData.roleIds" multiple placeholder="请选择角色" style="width: 100%">
            <el-option
              v-for="role in roleOptions"
              :key="role.id"
              :label="role.roleName"
              :value="role.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 重置密码对话框 -->
    <el-dialog v-model="resetPasswordDialogVisible" title="重置密码" width="400px">
      <el-form ref="resetPasswordFormRef" :model="resetPasswordForm" :rules="resetPasswordRules" label-width="100px">
        <el-form-item label="新密码" prop="password">
          <el-input v-model="resetPasswordForm.password" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resetPasswordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleResetPasswordSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import {
  getUserListApi,
  createUserApi,
  updateUserApi,
  deleteUserApi,
  resetPasswordApi,
  getUserRolesApi,
  type User,
  type UserListParams,
  type CreateUserParams,
  type UpdateUserParams,
} from '../../api/admin'
import { getAllRolesApi, type Role } from '../../api/admin'

const loading = ref(false)
const userList = ref<User[]>([])
const roleOptions = ref<Role[]>([])

const searchForm = reactive<UserListParams>({
  username: '',
  realName: '',
  status: undefined,
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0,
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const formData = reactive<CreateUserParams & { id?: number }>({
  username: '',
  password: '',
  phone: '',
  email: '',
  realName: '',
  gender: 0,
  status: 1,
  roleIds: [],
})

const formRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' },
  ],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
}

const resetPasswordDialogVisible = ref(false)
const resetPasswordFormRef = ref<FormInstance>()
const resetPasswordForm = reactive({
  userId: 0,
  password: '',
})

const resetPasswordRules = {
  password: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' },
  ],
}

const dialogTitle = computed(() => (isEdit.value ? '编辑用户' : '新增用户'))

// 加载用户列表
const loadUserList = async () => {
  loading.value = true
  try {
    const params: UserListParams = {
      current: pagination.current,
      size: pagination.size,
      ...searchForm,
    }
    const result = await getUserListApi(params)
    userList.value = result.records
    pagination.total = result.total
  } catch (error: any) {
    ElMessage.error(error.message || '加载用户列表失败')
  } finally {
    loading.value = false
  }
}

// 加载角色列表
const loadRoleOptions = async () => {
  try {
    roleOptions.value = await getAllRolesApi()
  } catch (error: any) {
    ElMessage.error(error.message || '加载角色列表失败')
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadUserList()
}

// 重置
const handleReset = () => {
  searchForm.username = ''
  searchForm.realName = ''
  searchForm.status = undefined
  handleSearch()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
  resetForm()
}

// 编辑
const handleEdit = async (row: User) => {
  isEdit.value = true
  dialogVisible.value = true
  formData.id = row.id
  formData.username = row.username
  formData.realName = row.realName || ''
  formData.phone = row.phone || ''
  formData.email = row.email || ''
  formData.gender = row.gender || 0
  formData.status = row.status
  formData.roleIds = []

  // 加载用户角色
  try {
    const roleIds = await getUserRolesApi(row.id)
    formData.roleIds = roleIds
  } catch (error: any) {
    ElMessage.error(error.message || '加载用户角色失败')
  }
}

// 删除
const handleDelete = async (row: User) => {
  try {
    await ElMessageBox.confirm(`确定要删除用户 "${row.username}" 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await deleteUserApi(row.id)
    ElMessage.success('删除成功')
    loadUserList()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 重置密码
const handleResetPassword = (row: User) => {
  resetPasswordForm.userId = row.id
  resetPasswordForm.password = ''
  resetPasswordDialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEdit.value) {
          const updateParams: UpdateUserParams = {
            phone: formData.phone,
            email: formData.email,
            realName: formData.realName,
            gender: formData.gender,
            status: formData.status,
            roleIds: formData.roleIds,
          }
          await updateUserApi(formData.id!, updateParams)
          ElMessage.success('更新成功')
        } else {
          const createParams: CreateUserParams = {
            username: formData.username,
            password: formData.password!,
            phone: formData.phone,
            email: formData.email,
            realName: formData.realName,
            gender: formData.gender,
            status: formData.status,
            roleIds: formData.roleIds,
          }
          await createUserApi(createParams)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        loadUserList()
      } catch (error: any) {
        ElMessage.error(error.message || '操作失败')
      }
    }
  })
}

// 重置密码提交
const handleResetPasswordSubmit = async () => {
  if (!resetPasswordFormRef.value) return
  await resetPasswordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await resetPasswordApi(resetPasswordForm.userId, { password: resetPasswordForm.password })
        ElMessage.success('重置密码成功')
        resetPasswordDialogVisible.value = false
      } catch (error: any) {
        ElMessage.error(error.message || '重置密码失败')
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  formData.id = undefined
  formData.username = ''
  formData.password = ''
  formData.phone = ''
  formData.email = ''
  formData.realName = ''
  formData.gender = 0
  formData.status = 1
  formData.roleIds = []
  formRef.value?.resetFields()
}

// 对话框关闭
const handleDialogClose = () => {
  resetForm()
}

// 分页大小改变
const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.current = 1
  loadUserList()
}

// 当前页改变
const handleCurrentChange = (current: number) => {
  pagination.current = current
  loadUserList()
}

onMounted(() => {
  loadUserList()
  loadRoleOptions()
})
</script>

<style scoped>
.user-manage {
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
