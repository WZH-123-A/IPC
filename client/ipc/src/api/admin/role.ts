import request, { type ApiResponse } from '../request'

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
  const response = await request.get<ApiResponse<{ records: Role[]; total: number; current: number; size: number }>>('/admin/role/list', { params })
  return response.data.data
}

/**
 * 获取所有角色（不分页）
 */
export const getAllRolesApi = async () => {
  const response = await request.get<ApiResponse<Role[]>>('/admin/role/all')
  return response.data.data
}

/**
 * 获取角色详情
 */
export const getRoleByIdApi = async (id: number) => {
  const response = await request.get<ApiResponse<Role>>(`/admin/role/${id}`)
  return response.data.data
}

/**
 * 新增角色
 */
export const createRoleApi = async (params: CreateRoleParams) => {
  const response = await request.post<ApiResponse<Role>>('/admin/role', params)
  return response.data.data
}

/**
 * 更新角色
 */
export const updateRoleApi = async (id: number, params: UpdateRoleParams) => {
  const response = await request.put<ApiResponse<Role>>(`/admin/role/${id}`, params)
  return response.data.data
}

/**
 * 删除角色
 */
export const deleteRoleApi = async (id: number) => {
  await request.delete<ApiResponse<void>>(`/admin/role/${id}`)
}

/**
 * 获取角色的权限ID列表
 */
export const getRolePermissionsApi = async (id: number) => {
  const response = await request.get<ApiResponse<number[]>>(`/admin/role/${id}/permissions`)
  return response.data.data
}

