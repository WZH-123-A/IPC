import request, { type ApiResponse } from '../request'

// ==================== 权限管理 ====================

// 权限树节点接口（用于管理端）
export interface PermissionTreeNode {
  id: number
  permissionCode: string
  permissionName: string
  permissionType: number
  parentId: number
  sort: number
  children?: PermissionTreeNode[]
}

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

