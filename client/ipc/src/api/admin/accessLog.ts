import request, { type ApiResponse } from '../request'

// ==================== 访问日志管理 ====================

export interface AccessLog {
  id: number
  userId?: number
  username?: string
  requestMethod: string
  requestUrl: string
  requestParams?: string
  ipAddress?: string
  userAgent?: string
  responseStatus?: number
  executionTime?: number
  createTime: string
}

export interface AccessLogListParams {
  current?: number
  size?: number
  username?: string
  requestUrl?: string
  requestMethod?: string
  responseStatus?: number
  startTime?: string
  endTime?: string
}

/**
 * 获取访问日志列表（分页）
 */
export const getAccessLogListApi = async (params: AccessLogListParams) => {
  const response = await request.get<ApiResponse<{ records: AccessLog[]; total: number; current: number; size: number }>>('/admin/access-log/list', { params })
  return response.data.data
}

/**
 * 获取访问日志详情
 */
export const getAccessLogByIdApi = async (id: number) => {
  const response = await request.get<ApiResponse<AccessLog>>(`/admin/access-log/${id}`)
  return response.data.data
}

/**
 * 删除访问日志
 */
export const deleteAccessLogApi = async (id: number) => {
  await request.delete<ApiResponse<void>>(`/admin/access-log/${id}`)
}

/**
 * 批量删除访问日志
 */
export const batchDeleteAccessLogApi = async (ids: number[]) => {
  await request.delete<ApiResponse<void>>('/admin/access-log/batch', { data: ids })
}

