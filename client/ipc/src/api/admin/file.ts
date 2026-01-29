import request, { type ApiResponse } from '../request'

// ==================== 文件管理 ====================

export interface SysFile {
  id: number
  fileName?: string
  filePath?: string
  fileUrl?: string
  fileType?: string
  fileSize?: number
  mimeType?: string
  uploadUserId?: number
  businessType?: string
  businessId?: number
  createTime: string
}

export interface SysFileListParams {
  current?: number
  size?: number
  fileName?: string
  fileType?: string
  businessType?: string
}

/**
 * 获取文件列表（分页）
 */
export const getFileListApi = async (params: SysFileListParams) => {
  const response = await request.get<
    ApiResponse<{ records: SysFile[]; total: number; current: number; size: number }>
  >('/admin/file/list', { params })
  return response.data.data
}

/**
 * 获取文件详情
 */
export const getFileByIdApi = async (id: number) => {
  const response = await request.get<ApiResponse<SysFile>>(`/admin/file/${id}`)
  return response.data.data
}

/**
 * 删除文件记录
 */
export const deleteFileApi = async (id: number) => {
  await request.delete<ApiResponse<void>>(`/admin/file/${id}`)
}

/**
 * 批量删除文件记录
 */
export const batchDeleteFileApi = async (ids: number[]) => {
  await request.delete<ApiResponse<void>>('/admin/file/batch', { data: ids })
}
