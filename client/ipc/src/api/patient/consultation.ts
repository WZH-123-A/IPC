import request, { type ApiResponse } from '../request'

// 问诊会话接口
export interface ConsultationSession {
  id: number
  sessionNo: string
  patientId: number
  doctorId?: number
  sessionType: number // 1-AI问诊 2-医生问诊
  title: string
  status: number // 0-进行中 1-已结束 2-已取消
  startTime: string
  endTime?: string
  createTime: string
  updateTime: string
}

// 问诊消息接口
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

// 医生简单信息接口
export interface DoctorSimple {
  id: number
  realName: string
  avatar?: string
  hospital?: string
  department?: string
  title?: string
  specialty?: string
  workYears?: number
}

// 创建问诊会话请求
export interface CreateConsultationRequest {
  sessionType: number // 1-AI问诊 2-医生问诊
  title: string
  doctorId?: number // 医生问诊时需要
}

// 发送消息请求
export interface SendMessageRequest {
  sessionId: number
  messageType: number
  content: string
}

// 分页响应
export interface PageResponse<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

/**
 * 获取问诊会话列表
 */
export const getConsultationSessions = async (params?: {
  current?: number
  size?: number
  status?: number
}): Promise<ApiResponse<PageResponse<ConsultationSession>>> => {
  const response = await request.get<ApiResponse<PageResponse<ConsultationSession>>>(
    '/patient/consultation/sessions',
    { params: { current: params?.current || 1, size: params?.size || 10, status: params?.status } }
  )
  return response.data
}

/**
 * 获取问诊会话详情
 */
export const getConsultationSession = async (
  sessionId: number
): Promise<ApiResponse<ConsultationSession>> => {
  const response = await request.get<ApiResponse<ConsultationSession>>(
    `/patient/consultation/sessions/${sessionId}`
  )
  return response.data
}

/**
 * 创建问诊会话
 */
export const createConsultationSession = async (
  data: CreateConsultationRequest
): Promise<ApiResponse<ConsultationSession>> => {
  const response = await request.post<ApiResponse<ConsultationSession>>(
    '/patient/consultation/sessions',
    data
  )
  return response.data
}

/**
 * 结束问诊会话
 */
export const endConsultationSession = async (
  sessionId: number
): Promise<ApiResponse<void>> => {
  const response = await request.post<ApiResponse<void>>(
    `/patient/consultation/sessions/${sessionId}/end`
  )
  return response.data
}

/**
 * 获取问诊消息列表
 */
export const getConsultationMessages = async (
  sessionId: number,
  params?: {
    current?: number
    size?: number
  }
): Promise<ApiResponse<PageResponse<ConsultationMessage>>> => {
  const response = await request.get<ApiResponse<PageResponse<ConsultationMessage>>>(
    `/patient/consultation/sessions/${sessionId}/messages`,
    { params: { current: params?.current || 1, size: params?.size || 50 } }
  )
  return response.data
}

/**
 * 发送消息
 */
export const sendMessage = async (
  data: SendMessageRequest
): Promise<ApiResponse<ConsultationMessage>> => {
  const response = await request.post<ApiResponse<ConsultationMessage>>(
    '/patient/consultation/messages',
    data
  )
  return response.data
}

/**
 * 标记消息为已读
 */
export const markMessageAsRead = async (
  messageId: number
): Promise<ApiResponse<void>> => {
  const response = await request.post<ApiResponse<void>>(
    `/patient/consultation/messages/${messageId}/read`
  )
  return response.data
}

/**
 * 获取可用的医生列表
 */
export const getAvailableDoctors = async (): Promise<ApiResponse<DoctorSimple[]>> => {
  const response = await request.get<ApiResponse<DoctorSimple[]>>(
    '/patient/consultation/doctors'
  )
  return response.data
}

