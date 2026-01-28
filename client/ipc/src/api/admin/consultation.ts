import request, { type ApiResponse } from '../request'

// ==================== 问诊会话管理 ====================

export interface ConsultationSession {
  id: number
  sessionNo: string
  patientId: number
  patientName?: string
  doctorId?: number
  doctorName?: string
  sessionType: number // 1-AI问诊 2-医生问诊
  title?: string
  status: number // 0-进行中 1-已结束 2-已取消
  startTime?: string
  endTime?: string
  createTime: string
  updateTime?: string
}

export interface ConsultationSessionListParams {
  current?: number
  size?: number
  sessionNo?: string
  patientId?: number
  doctorId?: number
  sessionType?: number
  status?: number
}

/**
 * 获取问诊会话列表（分页）
 */
export const getConsultationSessionListApi = async (params: ConsultationSessionListParams) => {
  const response = await request.get<ApiResponse<{ records: ConsultationSession[]; total: number; current: number; size: number }>>(
    '/admin/consultation/sessions/list',
    { params }
  )
  return response.data.data
}

/**
 * 获取问诊会话详情
 */
export const getConsultationSessionByIdApi = async (id: number) => {
  const response = await request.get<ApiResponse<ConsultationSession>>(`/admin/consultation/sessions/${id}`)
  return response.data.data
}

