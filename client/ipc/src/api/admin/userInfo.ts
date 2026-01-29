import request, { type ApiResponse } from '../request'

// ==================== 医生信息（管理员） ====================

export interface AdminDoctorInfoItem {
  id: number
  userId: number
  username?: string
  realName?: string
  phone?: string
  hospital?: string
  department?: string
  title?: string
  specialty?: string
  licenseNo?: string
  workYears?: number
  createTime?: string
  updateTime?: string
}

export interface AdminDoctorInfoListResult {
  records: AdminDoctorInfoItem[]
  total: number
  current: number
  size: number
}

export interface AdminDoctorInfoListParams {
  current?: number
  size?: number
  username?: string
  realName?: string
  hospital?: string
  department?: string
}

export interface UserOptionItem {
  id: number
  username?: string
  realName?: string
}

export interface AdminDoctorInfoSaveParams {
  userId: number
  hospital?: string
  department?: string
  title?: string
  specialty?: string
  licenseNo?: string
  workYears?: number
}

/**
 * 获取医生信息列表（分页）
 */
export function getDoctorInfoListApi(
  params: AdminDoctorInfoListParams
): Promise<AdminDoctorInfoListResult | undefined> {
  return request
    .get<ApiResponse<AdminDoctorInfoListResult>>('/admin/user-info/doctor/list', { params })
    .then((res) => res.data?.data)
}

/**
 * 可选医生用户（用于新增下拉）
 */
export function getDoctorUserOptionsApi(): Promise<UserOptionItem[] | undefined> {
  return request
    .get<ApiResponse<UserOptionItem[]>>('/admin/user-info/doctor/user-options')
    .then((res) => res.data?.data)
}

/**
 * 新增医生信息
 */
export function createDoctorInfoApi(data: AdminDoctorInfoSaveParams): Promise<void> {
  return request.post<ApiResponse<void>>('/admin/user-info/doctor', data).then(() => undefined)
}

/**
 * 更新医生信息
 */
export function updateDoctorInfoApi(id: number, data: AdminDoctorInfoSaveParams): Promise<void> {
  return request.put<ApiResponse<void>>(`/admin/user-info/doctor/${id}`, data).then(() => undefined)
}

/**
 * 删除医生信息（逻辑删除）
 */
export function deleteDoctorInfoApi(id: number): Promise<void> {
  return request.delete<ApiResponse<void>>(`/admin/user-info/doctor/${id}`).then(() => undefined)
}

// ==================== 患者信息（管理员） ====================

export interface AdminPatientInfoItem {
  id: number
  userId: number
  username?: string
  realName?: string
  phone?: string
  age?: number
  address?: string
  medicalHistory?: string
  allergyHistory?: string
  createTime?: string
  updateTime?: string
}

export interface AdminPatientInfoListResult {
  records: AdminPatientInfoItem[]
  total: number
  current: number
  size: number
}

export interface AdminPatientInfoListParams {
  current?: number
  size?: number
  username?: string
  realName?: string
  age?: number
}

export interface AdminPatientInfoSaveParams {
  userId: number
  age?: number
  address?: string
  medicalHistory?: string
  allergyHistory?: string
}

/**
 * 获取患者信息列表（分页）
 */
export function getPatientInfoListApi(
  params: AdminPatientInfoListParams
): Promise<AdminPatientInfoListResult | undefined> {
  return request
    .get<ApiResponse<AdminPatientInfoListResult>>('/admin/user-info/patient/list', { params })
    .then((res) => res.data?.data)
}

/**
 * 可选患者用户（用于新增下拉）
 */
export function getPatientUserOptionsApi(): Promise<UserOptionItem[] | undefined> {
  return request
    .get<ApiResponse<UserOptionItem[]>>('/admin/user-info/patient/user-options')
    .then((res) => res.data?.data)
}

/**
 * 新增患者信息
 */
export function createPatientInfoApi(data: AdminPatientInfoSaveParams): Promise<void> {
  return request.post<ApiResponse<void>>('/admin/user-info/patient', data).then(() => undefined)
}

/**
 * 更新患者信息
 */
export function updatePatientInfoApi(id: number, data: AdminPatientInfoSaveParams): Promise<void> {
  return request.put<ApiResponse<void>>(`/admin/user-info/patient/${id}`, data).then(() => undefined)
}

/**
 * 删除患者信息（逻辑删除）
 */
export function deletePatientInfoApi(id: number): Promise<void> {
  return request.delete<ApiResponse<void>>(`/admin/user-info/patient/${id}`).then(() => undefined)
}
