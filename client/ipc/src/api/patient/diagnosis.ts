import request, { type ApiResponse } from '../request'

// 诊断记录接口
export interface DiagnosisRecord {
  id: number
  userId: number
  imageUrl: string
  imageName: string
  bodyPart?: string
  modelVersion: string
  status: number // 0-识别中 1-识别成功 2-识别失败
  createTime: string
  updateTime: string
}

// 诊断结果接口
export interface DiagnosisResult {
  id: number
  recordId: number
  diseaseId: number
  diseaseName?: string
  confidence: number
  rank: number
  createTime: string
}

// 疾病类型接口
export interface DiseaseType {
  id: number
  name: string
  description?: string
  category?: string
}

// 上传诊断图片请求
export interface UploadDiagnosisImageRequest {
  image: File
  bodyPart?: string
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
 * 上传诊断图片
 */
export const uploadDiagnosisImage = async (
  data: UploadDiagnosisImageRequest
): Promise<ApiResponse<DiagnosisRecord>> => {
  const formData = new FormData()
  formData.append('image', data.image)
  if (data.bodyPart) {
    formData.append('bodyPart', data.bodyPart)
  }

  const response = await request.post<ApiResponse<DiagnosisRecord>>(
    '/patient/diagnosis/upload',
    formData,
    {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    }
  )
  return response.data
}

/**
 * 获取诊断记录列表
 */
export const getDiagnosisRecords = async (params?: {
  current?: number
  size?: number
}): Promise<ApiResponse<PageResponse<DiagnosisRecord>>> => {
  const response = await request.get<ApiResponse<PageResponse<DiagnosisRecord>>>(
    '/patient/diagnosis/records',
    { params: { current: params?.current || 1, size: params?.size || 10 } }
  )
  return response.data
}

/**
 * 获取诊断记录详情
 */
export const getDiagnosisRecord = async (
  recordId: number
): Promise<ApiResponse<DiagnosisRecord>> => {
  const response = await request.get<ApiResponse<DiagnosisRecord>>(
    `/patient/diagnosis/records/${recordId}`
  )
  return response.data
}

/**
 * 获取诊断结果
 */
export const getDiagnosisResults = async (
  recordId: number
): Promise<ApiResponse<DiagnosisResult[]>> => {
  const response = await request.get<ApiResponse<DiagnosisResult[]>>(
    `/patient/diagnosis/records/${recordId}/results`
  )
  return response.data
}

/**
 * 获取疾病类型列表
 */
export const getDiseaseTypes = async (): Promise<ApiResponse<DiseaseType[]>> => {
  const response = await request.get<ApiResponse<DiseaseType[]>>(
    '/patient/diagnosis/disease-types'
  )
  return response.data
}

