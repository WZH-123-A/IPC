import request, { type ApiResponse } from '../request'

// ==================== 操作日志管理 ====================

export interface OperationLog {
  id: number
  userId?: number
  username?: string
  operationType?: string
  operationModule?: string
  operationDesc?: string
  requestMethod?: string
  requestUrl?: string
  requestParams?: string
  responseData?: string
  ipAddress?: string
  userAgent?: string
  status?: number // 0-失败 1-成功
  errorMsg?: string
  executionTime?: number
  createTime: string
}

export interface OperationLogListParams {
  current?: number
  size?: number
  username?: string
  operationType?: string
  operationModule?: string
  status?: number
  startTime?: string
  endTime?: string
}

/**
 * 获取操作日志列表（分页）
 */
export const getOperationLogListApi = async (params: OperationLogListParams) => {
  const response = await request.get<ApiResponse<{ records: OperationLog[]; total: number; current: number; size: number }>>('/admin/operation-log/list', { params })
  return response.data.data
}

/**
 * 获取操作日志详情
 */
export const getOperationLogByIdApi = async (id: number) => {
  const response = await request.get<ApiResponse<OperationLog>>(`/admin/operation-log/${id}`)
  return response.data.data
}

/**
 * 删除操作日志
 */
export const deleteOperationLogApi = async (id: number) => {
  await request.delete<ApiResponse<void>>(`/admin/operation-log/${id}`)
}

/**
 * 批量删除操作日志
 */
export const batchDeleteOperationLogApi = async (ids: number[]) => {
  await request.delete<ApiResponse<void>>('/admin/operation-log/batch', { data: ids })
}

