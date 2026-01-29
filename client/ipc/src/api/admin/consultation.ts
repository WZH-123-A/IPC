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

export interface ConsultationSessionUpdateParams {
  title?: string
  status?: number
}

/**
 * 管理员更新问诊会话
 */
export const updateConsultationSessionApi = async (id: number, data: ConsultationSessionUpdateParams) => {
  await request.put<ApiResponse<void>>(`/admin/consultation/sessions/${id}`, data)
}

/**
 * 管理员删除问诊会话
 */
export const deleteConsultationSessionApi = async (id: number) => {
  await request.delete<ApiResponse<void>>(`/admin/consultation/sessions/${id}`)
}

// ==================== 问诊消息管理 ====================

export interface ConsultationMessage {
  id: number
  sessionId: number
  senderId: number
  senderType: number // 1-患者 2-医生 3-AI
  messageType: number // 1-文本 2-图片 3-语音 4-视频
  content: string
  aiModel?: string
  isRead: number
  createTime: string
}

export interface ConsultationMessageListParams {
  current?: number
  size?: number
}

/**
 * 管理员获取指定会话的问诊消息列表（分页）
 */
export const getConsultationMessageListApi = async (
  sessionId: number,
  params: ConsultationMessageListParams
) => {
  const response = await request.get<ApiResponse<{
    records: ConsultationMessage[]
    total: number
    current: number
    size: number
  }>>(`/admin/consultation/sessions/${sessionId}/messages`, { params })
  return response.data.data
}

/**
 * 管理员删除问诊消息
 */
export const deleteConsultationMessageApi = async (id: number) => {
  await request.delete<ApiResponse<void>>(`/admin/consultation/messages/${id}`)
}

// ==================== 问诊评价管理 ====================

export interface ConsultationEvaluation {
  id: number
  sessionId: number
  sessionNo?: string
  patientId: number
  patientName?: string
  doctorId?: number
  doctorName?: string
  rating: number
  comment?: string
  createTime: string
}

export interface ConsultationEvaluationListParams {
  current?: number
  size?: number
  sessionId?: number
  patientId?: number
  doctorId?: number
  rating?: number
}

/**
 * 管理员获取问诊评价列表（分页）
 */
export const getConsultationEvaluationListApi = async (
  params: ConsultationEvaluationListParams
) => {
  const response = await request.get<ApiResponse<{
    records: ConsultationEvaluation[]
    total: number
    current: number
    size: number
  }>>('/admin/consultation/evaluations/list', { params })
  return response.data.data
}

export interface ConsultationEvaluationCreateParams {
  sessionId: number
  patientId: number
  doctorId?: number
  rating: number
  comment?: string
}

export interface ConsultationEvaluationUpdateParams {
  rating?: number
  comment?: string
}

/**
 * 管理员新增问诊评价
 */
export const createConsultationEvaluationApi = async (data: ConsultationEvaluationCreateParams) => {
  const response = await request.post<ApiResponse<ConsultationEvaluation>>('/admin/consultation/evaluations', data)
  return response.data.data
}

/**
 * 管理员更新问诊评价
 */
export const updateConsultationEvaluationApi = async (
  id: number,
  data: ConsultationEvaluationUpdateParams
) => {
  await request.put<ApiResponse<void>>(`/admin/consultation/evaluations/${id}`, data)
}

/**
 * 管理员删除问诊评价
 */
export const deleteConsultationEvaluationApi = async (id: number) => {
  await request.delete<ApiResponse<void>>(`/admin/consultation/evaluations/${id}`)
}

