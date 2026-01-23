import request, { type ApiResponse } from './request'

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

// ==================== 角色管理 ====================

export interface Role {
  id: number
  roleCode: string
  roleName: string
  roleDesc?: string
  createTime: string
  updateTime: string
}

export interface RoleListParams {
  current?: number
  size?: number
  roleCode?: string
  roleName?: string
}

export interface CreateRoleParams {
  roleCode: string
  roleName: string
  roleDesc?: string
  permissionIds?: number[]
}

export interface UpdateRoleParams {
  roleCode?: string
  roleName?: string
  roleDesc?: string
  permissionIds?: number[]
}

/**
 * 获取角色列表（分页）
 */
export const getRoleListApi = async (params: RoleListParams) => {
  const response = await request.get<ApiResponse<{ records: Role[]; total: number; current: number; size: number }>>('/role/list', { params })
  return response.data.data
}

/**
 * 获取所有角色（不分页）
 */
export const getAllRolesApi = async () => {
  const response = await request.get<ApiResponse<Role[]>>('/role/all')
  return response.data.data
}

/**
 * 获取角色详情
 */
export const getRoleByIdApi = async (id: number) => {
  const response = await request.get<ApiResponse<Role>>(`/role/${id}`)
  return response.data.data
}

/**
 * 新增角色
 */
export const createRoleApi = async (params: CreateRoleParams) => {
  const response = await request.post<ApiResponse<Role>>('/role', params)
  return response.data.data
}

/**
 * 更新角色
 */
export const updateRoleApi = async (id: number, params: UpdateRoleParams) => {
  const response = await request.put<ApiResponse<Role>>(`/role/${id}`, params)
  return response.data.data
}

/**
 * 删除角色
 */
export const deleteRoleApi = async (id: number) => {
  await request.delete<ApiResponse<void>>(`/role/${id}`)
}

/**
 * 获取角色的权限ID列表
 */
export const getRolePermissionsApi = async (id: number) => {
  const response = await request.get<ApiResponse<number[]>>(`/role/${id}/permissions`)
  return response.data.data
}

// ==================== 权限管理 ====================

export interface Permission {
  id: number
  permissionCode: string
  permissionName: string
  permissionType: number // 1-菜单 2-按钮 3-接口 4-路由
  parentId: number
  sort: number
  createTime: string
  updateTime: string
}

export interface PermissionTreeNode {
  id: number
  permissionCode: string
  permissionName: string
  permissionType: number
  parentId: number
  sort: number
  children?: PermissionTreeNode[]
}

export interface PermissionListParams {
  permissionType?: number
}

export interface CreatePermissionParams {
  permissionCode: string
  permissionName: string
  permissionType: number
  parentId?: number
  sort?: number
}

export interface UpdatePermissionParams {
  permissionName?: string
  permissionType?: number
  parentId?: number
  sort?: number
}

/**
 * 获取权限树
 */
export const getPermissionTreeApi = async (params?: PermissionListParams) => {
  const response = await request.get<ApiResponse<PermissionTreeNode[]>>('/permission/tree', { params })
  return response.data.data
}

/**
 * 获取权限列表
 */
export const getPermissionListApi = async (params?: PermissionListParams) => {
  const response = await request.get<ApiResponse<Permission[]>>('/permission/list', { params })
  return response.data.data
}

/**
 * 获取权限详情
 */
export const getPermissionByIdApi = async (id: number) => {
  const response = await request.get<ApiResponse<Permission>>(`/permission/${id}`)
  return response.data.data
}

/**
 * 新增权限
 */
export const createPermissionApi = async (params: CreatePermissionParams) => {
  const response = await request.post<ApiResponse<Permission>>('/permission', params)
  return response.data.data
}

/**
 * 更新权限
 */
export const updatePermissionApi = async (id: number, params: UpdatePermissionParams) => {
  const response = await request.put<ApiResponse<Permission>>(`/permission/${id}`, params)
  return response.data.data
}

/**
 * 删除权限
 */
export const deletePermissionApi = async (id: number) => {
  await request.delete<ApiResponse<void>>(`/permission/${id}`)
}

