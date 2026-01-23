<template>
  <BaseManageView
    ref="baseRef"
    title="用户管理"
    :show-add-button="true"
    :show-selection="false"
    @search="handleSearch"
    @add="handleAdd"
    @edit="(row) => handleEdit(row as unknown as User)"
    @delete="(row) => handleDelete(row as unknown as User)"
    @submit="handleSubmit"
  >
    <!-- 自定义搜索表单 -->
    <template #search-form="{ searchForm }">
      <el-form-item label="用户名">
        <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
      </el-form-item>
      <el-form-item label="真实姓名">
        <el-input v-model="searchForm.realName" placeholder="请输入真实姓名" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select
          v-model="searchForm.status"
          placeholder="请选择状态"
          clearable
          style="width: 150px"
        >
          <el-option label="正常" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
      </el-form-item>
    </template>

    <!-- 自定义表格列 -->
    <template #table-columns>
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
    </template>

    <!-- 自定义操作按钮 -->
    <template #actions="{ row }">
      <el-button type="primary" link size="small" @click="handleEdit(row as User)">编辑</el-button>
      <el-button type="warning" link size="small" @click="handleResetPassword(row as User)"
        >重置密码</el-button
      >
      <el-button type="danger" link size="small" @click="handleDelete(row as User)">删除</el-button>
    </template>

    <!-- 自定义对话框表单 -->
    <template #dialog-form="{ formData }">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" :disabled="isEdit" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input
            v-model="formData.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
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
          <el-select
            v-model="formData.roleIds"
            multiple
            placeholder="请选择角色"
            style="width: 100%"
          >
            <el-option
              v-for="role in roleOptions"
              :key="role.id"
              :label="role.roleName"
              :value="role.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
    </template>
  </BaseManageView>

  <!-- 重置密码对话框 -->
  <el-dialog v-model="resetPasswordDialogVisible" title="重置密码" width="400px">
    <el-form
      ref="resetPasswordFormRef"
      :model="resetPasswordForm"
      :rules="resetPasswordRules"
      label-width="100px"
    >
      <el-form-item label="新密码" prop="password">
        <el-input
          v-model="resetPasswordForm.password"
          type="password"
          placeholder="请输入新密码"
          show-password
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="resetPasswordDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleResetPasswordSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import BaseManageView from '../../../components/admin/BaseManageView.vue'
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
} from '../../../api/admin/user'
import { getAllRolesApi, type Role } from '../../../api/admin/role'

const baseRef = ref<InstanceType<typeof BaseManageView>>()
const formRef = ref<FormInstance>()
const resetPasswordFormRef = ref<FormInstance>()
const roleOptions = ref<Role[]>([])
const resetPasswordDialogVisible = ref(false)
const resetPasswordForm = reactive({
  userId: 0,
  password: '',
})
const isEdit = ref(false)

const formRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' },
  ],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
}

const resetPasswordRules = {
  password: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' },
  ],
}

// 加载用户列表
const loadUserList = async () => {
  if (!baseRef.value) return

  baseRef.value.setLoading(true)
  try {
    const searchForm = baseRef.value.searchForm as UserListParams
    const pagination = baseRef.value.pagination

    const params: UserListParams = {
      current: pagination.current,
      size: pagination.size,
      ...searchForm,
    }
    const result = await getUserListApi(params)
    baseRef.value.setDataList(result.records as unknown as Record<string, unknown>[])
    baseRef.value.setPagination({
      current: result.current,
      size: result.size,
      total: result.total,
    })
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : '加载用户列表失败'
    ElMessage.error(message)
  } finally {
    baseRef.value.setLoading(false)
  }
}

// 加载角色列表
const loadRoleOptions = async () => {
  try {
    roleOptions.value = await getAllRolesApi()
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : '加载角色列表失败'
    ElMessage.error(message)
  }
}

// 搜索
const handleSearch = () => {
  loadUserList()
}

// 新增
const handleAdd = () => {
  if (!baseRef.value) return
  isEdit.value = false
  baseRef.value.dialogVisible = true
  baseRef.value.formData = {
    username: '',
    password: '',
    phone: '',
    email: '',
    realName: '',
    gender: 0,
    status: 1,
    roleIds: [],
  }
}

// 编辑
const handleEdit = async (row: User) => {
  if (!baseRef.value) return
  isEdit.value = true
  baseRef.value.dialogVisible = true
  baseRef.value.formData = {
    id: row.id,
    username: row.username,
    realName: row.realName || '',
    phone: row.phone || '',
    email: row.email || '',
    gender: row.gender || 0,
    status: row.status,
    roleIds: [],
  }

  // 加载用户角色
  try {
    const roleIds = await getUserRolesApi(row.id)
    baseRef.value.formData.roleIds = roleIds
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : '加载用户角色失败'
    ElMessage.error(message)
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
  } catch (error: unknown) {
    if (error !== 'cancel') {
      const message = error instanceof Error ? error.message : '删除失败'
      ElMessage.error(message)
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
const handleSubmit = async (formData: Record<string, unknown>) => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEdit.value) {
          const updateParams: UpdateUserParams = {
            phone: formData.phone as string,
            email: formData.email as string,
            realName: formData.realName as string,
            gender: formData.gender as number,
            status: formData.status as number,
            roleIds: formData.roleIds as number[],
          }
          await updateUserApi(formData.id as number, updateParams)
          ElMessage.success('更新成功')
        } else {
          const createParams: CreateUserParams = {
            username: formData.username as string,
            password: formData.password as string,
            phone: formData.phone as string,
            email: formData.email as string,
            realName: formData.realName as string,
            gender: formData.gender as number,
            status: formData.status as number,
            roleIds: formData.roleIds as number[],
          }
          await createUserApi(createParams)
          ElMessage.success('新增成功')
        }
        if (baseRef.value) {
          baseRef.value.dialogVisible = false
        }
        loadUserList()
      } catch (error: unknown) {
        const message = error instanceof Error ? error.message : '操作失败'
        ElMessage.error(message)
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
      } catch (error: unknown) {
        const message = error instanceof Error ? error.message : '重置密码失败'
        ElMessage.error(message)
      }
    }
  })
}

onMounted(() => {
  // 初始化搜索表单
  if (baseRef.value) {
    baseRef.value.searchForm = {
      username: '',
      realName: '',
      status: undefined,
    }
  }
  loadUserList()
  loadRoleOptions()
})
</script>

<style scoped></style>
