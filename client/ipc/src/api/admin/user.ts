import request, { type ApiResponse } from '../request'

// ==================== 用户管理 ====================

export interface User {
  id: number
  username: string
  phone?: string
  email?: string
  realName?: string
  gender?: number // 0-未知 1-男 2-女
  avatar?: string
  status: number // 0-禁用 1-正常
  lastLoginTime?: string
  createTime: string
  updateTime: string
}

export interface UserListParams {
  current?: number
  size?: number
  username?: string
  realName?: string
  status?: number
}

export interface CreateUserParams {
  username: string
  password: string
  phone?: string
  email?: string
  realName?: string
  gender?: number
  status?: number
  roleIds?: number[]
}

export interface UpdateUserParams {
  phone?: string
  email?: string
  realName?: string
  gender?: number
  status?: number
  roleIds?: number[]
}

export interface ResetPasswordParams {
  password: string
}

/**
 * 获取用户列表（分页）
 */
export const getUserListApi = async (params: UserListParams) => {
  const response = await request.get<ApiResponse<{ records: User[]; total: number; current: number; size: number }>>('/user/list', { params })
  return response.data.data
}

/**
 * 获取用户详情
 */
export const getUserByIdApi = async (id: number) => {
  const response = await request.get<ApiResponse<User>>(`/user/${id}`)
  return response.data.data
}

/**
 * 新增用户
 */
export const createUserApi = async (params: CreateUserParams) => {
  const response = await request.post<ApiResponse<User>>('/user', params)
  return response.data.data
}

/**
 * 更新用户
 */
export const updateUserApi = async (id: number, params: UpdateUserParams) => {
  const response = await request.put<ApiResponse<User>>(`/user/${id}`, params)
  return response.data.data
}

/**
 * 删除用户
 */
export const deleteUserApi = async (id: number) => {
  await request.delete<ApiResponse<void>>(`/user/${id}`)
}

/**
 * 重置用户密码
 */
export const resetPasswordApi = async (id: number, params: ResetPasswordParams) => {
  await request.put<ApiResponse<void>>(`/user/${id}/reset-password`, params)
}

/**
 * 获取用户的角色ID列表
 */
export const getUserRolesApi = async (id: number) => {
  const response = await request.get<ApiResponse<number[]>>(`/user/${id}/roles`)
  return response.data.data
}

