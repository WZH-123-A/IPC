import request, { type ApiResponse } from './request'

export interface FileUploadResponse {
  fileId: number
  fileName: string
  fileUrl: string
  mimeType?: string
  fileSize?: number
  fileType?: string
}

/**
 * 通用上传（multipart/form-data）
 * @param url 后端上传接口路径（例如：/patient/consultation/upload）
 * @param file 文件
 * @param fieldName 表单字段名（默认 file）
 */
export const uploadFile = async (
  url: string,
  file: File,
  fieldName: string = 'file',
): Promise<ApiResponse<FileUploadResponse>> => {
  const formData = new FormData()
  formData.append(fieldName, file)

  const response = await request.post<ApiResponse<FileUploadResponse>>(url, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
  return response.data
}

/**
 * 患者端：上传问诊聊天文件
 */
export const uploadPatientConsultationFile = async (
  file: File,
): Promise<ApiResponse<FileUploadResponse>> => {
  return uploadFile('/patient/consultation/upload', file)
}

/**
 * 医生端：上传问诊聊天文件
 */
export const uploadDoctorConsultationFile = async (
  file: File,
): Promise<FileUploadResponse> => {
  const resp = await uploadFile('/doctor/consultations/upload', file)
  return resp.data
}

