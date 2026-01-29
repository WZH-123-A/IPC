import request, { type ApiResponse } from '../request'

// ==================== 病知识库 ====================

export interface KnowledgeCategory {
  id: number
  categoryCode: string
  categoryName: string
  icon?: string
  description?: string
  sort?: number
}

export interface KnowledgeContentItem {
  id: number
  categoryId: number
  categoryName?: string
  title: string
  subtitle?: string
  contentType: number // 1-图文 2-视频 3-音频 4-文档
  coverImage?: string
  source?: string
  author?: string
  viewCount?: number
  likeCount?: number
  publishTime?: string
}

export interface KnowledgeContentDetail extends KnowledgeContentItem {
  content?: string
  videoUrl?: string
  audioUrl?: string
  documentUrl?: string
  shareCount?: number
  createTime?: string
}

export interface KnowledgeContentListParams {
  current?: number
  size?: number
  categoryId?: number
  keyword?: string
}

export interface KnowledgeContentListResponse {
  records: KnowledgeContentItem[]
  total: number
  current: number
  size: number
}

/**
 * 获取知识库分类列表
 */
export const getKnowledgeCategories = async (): Promise<
  ApiResponse<KnowledgeCategory[]>
> => {
  const response = await request.get<ApiResponse<KnowledgeCategory[]>>(
    '/patient/knowledge/categories'
  )
  return response.data
}

/**
 * 分页查询知识库内容
 */
export const getKnowledgeContents = async (
  params: KnowledgeContentListParams
): Promise<ApiResponse<KnowledgeContentListResponse>> => {
  const response = await request.get<
    ApiResponse<KnowledgeContentListResponse>
  >('/patient/knowledge/contents', { params })
  return response.data
}

/**
 * 获取知识库内容详情
 */
export const getKnowledgeContentDetail = async (
  id: number
): Promise<ApiResponse<KnowledgeContentDetail>> => {
  const response = await request.get<ApiResponse<KnowledgeContentDetail>>(
    `/patient/knowledge/contents/${id}`
  )
  return response.data
}
