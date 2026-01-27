import request, { type ApiResponse } from './request'

// ==================== 医生管理 ====================

export interface DoctorInfo {
  id: number
  username: string
  realName?: string
  phone?: string
  email?: string
  gender?: number // 0-未知 1-男 2-女
  avatar?: string
  hospital?: string
  department?: string
  title?: string
  specialty?: string
  licenseNo?: string
  workYears?: number
  createTime: string
}

export interface DoctorHome {
  doctorInfo: DoctorInfo
  todayConsultationCount: number
  ongoingConsultationCount: number
  totalPatientCount: number
  monthConsultationCount: number
}

export interface Patient {
  id: number
  username: string
  realName?: string
  phone?: string
  email?: string
  gender?: number
  avatar?: string
  age?: number
  address?: string
  medicalHistory?: string
  allergyHistory?: string
  createTime: string
}

export interface PatientListParams {
  current?: number
  size?: number
  realName?: string
  username?: string
  phone?: string
}

export interface Consultation {
  id: number
  sessionNo: string
  patientId: number
  patientName?: string
  doctorId?: number
  sessionType: number // 1-AI问诊 2-医生问诊
  title?: string
  status: number // 0-进行中 1-已结束 2-已取消
  startTime?: string
  endTime?: string
  createTime: string
  unreadCount?: number
}

export interface ConsultationListParams {
  current?: number
  size?: number
  patientName?: string
  title?: string
  status?: number
  sessionType?: number
  startTime?: string
  endTime?: string
}

/**
 * 获取医生首页数据
 */
export const getDoctorHomeApi = async () => {
  const response = await request.get<ApiResponse<DoctorHome>>('/doctor/home')
  return response.data.data
}

/**
 * 获取患者列表（分页）
 */
export const getPatientListApi = async (params: PatientListParams) => {
  const response = await request.get<ApiResponse<{ records: Patient[]; total: number; current: number; size: number }>>('/doctor/patients', { params })
  return response.data.data
}

/**
 * 获取问诊列表（分页）
 */
export const getConsultationListApi = async (params: ConsultationListParams) => {
  const response = await request.get<ApiResponse<{ records: Consultation[]; total: number; current: number; size: number }>>('/doctor/consultations', { params })
  return response.data.data
}
