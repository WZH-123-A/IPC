import request, { type ApiResponse } from '../request'
import { uploadFile } from '../upload'
import type { FileUploadResponse } from '../upload'

// ==================== 分类 ====================

export interface KnowledgeCategory {
  id: number
  categoryCode?: string
  categoryName?: string
  parentId?: number
  sort?: number
  icon?: string
  description?: string
  createTime?: string
  updateTime?: string
}

export interface KnowledgeCategoryListParams {
  current?: number
  size?: number
  categoryName?: string
  categoryCode?: string
}

export interface KnowledgeCategoryListResult {
  records: KnowledgeCategory[]
  total: number
  current: number
  size: number
}

export interface KnowledgeCategoryCreateParams {
  categoryCode: string
  categoryName: string
  parentId?: number
  sort?: number
  icon?: string
  description?: string
}

export interface KnowledgeCategoryUpdateParams {
  categoryName?: string
  parentId?: number
  sort?: number
  icon?: string
  description?: string
}

export const getCategoryListApi = async (params: KnowledgeCategoryListParams) => {
  const response = await request.get<ApiResponse<KnowledgeCategoryListResult>>(
    '/admin/knowledge/categories/list',
    { params }
  )
  return response.data.data
}

export const getCategoryByIdApi = async (id: number) => {
  const response = await request.get<ApiResponse<KnowledgeCategory>>(
    `/admin/knowledge/categories/${id}`
  )
  return response.data.data
}

export const createCategoryApi = async (data: KnowledgeCategoryCreateParams) => {
  const response = await request.post<ApiResponse<KnowledgeCategory>>(
    '/admin/knowledge/categories',
    data
  )
  return response.data.data
}

export const updateCategoryApi = async (
  id: number,
  data: KnowledgeCategoryUpdateParams
) => {
  await request.put(`/admin/knowledge/categories/${id}`, data)
}

export const deleteCategoryApi = async (id: number) => {
  await request.delete(`/admin/knowledge/categories/${id}`)
}

// ==================== 内容 ====================

export interface KnowledgeContent {
  id: number
  categoryId?: number
  categoryName?: string
  title?: string
  subtitle?: string
  contentType?: number
  coverImage?: string
  content?: string
  videoUrl?: string
  audioUrl?: string
  documentUrl?: string
  source?: string
  author?: string
  viewCount?: number
  likeCount?: number
  shareCount?: number
  status?: number
  publishTime?: string
  createTime?: string
  updateTime?: string
  /** 关联的标签ID列表 */
  tagIds?: number[]
}

export interface KnowledgeContentListParams {
  current?: number
  size?: number
  categoryId?: number
  title?: string
  status?: number
}

export interface KnowledgeContentListResult {
  records: KnowledgeContent[]
  total: number
  current: number
  size: number
}

export interface KnowledgeContentCreateParams {
  categoryId: number
  title: string
  subtitle?: string
  contentType: number
  coverImage?: string
  content?: string
  videoUrl?: string
  audioUrl?: string
  documentUrl?: string
  source?: string
  author?: string
  status?: number
  tagIds?: number[]
}

export interface KnowledgeContentUpdateParams {
  categoryId?: number
  title?: string
  subtitle?: string
  contentType?: number
  coverImage?: string
  content?: string
  videoUrl?: string
  audioUrl?: string
  documentUrl?: string
  source?: string
  author?: string
  status?: number
  tagIds?: number[]
}

export const getContentListApi = async (params: KnowledgeContentListParams) => {
  const response = await request.get<ApiResponse<KnowledgeContentListResult>>(
    '/admin/knowledge/contents/list',
    { params }
  )
  return response.data.data
}

export const getContentByIdApi = async (id: number) => {
  const response = await request.get<ApiResponse<KnowledgeContent>>(
    `/admin/knowledge/contents/${id}`
  )
  return response.data.data
}

export const createContentApi = async (data: KnowledgeContentCreateParams) => {
  const response = await request.post<ApiResponse<KnowledgeContent>>(
    '/admin/knowledge/contents',
    data
  )
  return response.data.data
}

export const updateContentApi = async (
  id: number,
  data: KnowledgeContentUpdateParams
) => {
  await request.put(`/admin/knowledge/contents/${id}`, data)
}

export const deleteContentApi = async (id: number) => {
  await request.delete(`/admin/knowledge/contents/${id}`)
}

// ==================== 标签 ====================

export interface KnowledgeTag {
  id: number
  tagName?: string
  tagColor?: string
  useCount?: number
  createTime?: string
  updateTime?: string
}

export interface KnowledgeTagListParams {
  current?: number
  size?: number
  tagName?: string
}

export interface KnowledgeTagListResult {
  records: KnowledgeTag[]
  total: number
  current: number
  size: number
}

export interface KnowledgeTagCreateParams {
  tagName: string
  tagColor?: string
}

export interface KnowledgeTagUpdateParams {
  tagName?: string
  tagColor?: string
}

export const getTagListApi = async (params: KnowledgeTagListParams) => {
  const response = await request.get<ApiResponse<KnowledgeTagListResult>>(
    '/admin/knowledge/tags/list',
    { params }
  )
  return response.data.data
}

export const getTagByIdApi = async (id: number) => {
  const response = await request.get<ApiResponse<KnowledgeTag>>(
    `/admin/knowledge/tags/${id}`
  )
  return response.data.data
}

export const createTagApi = async (data: KnowledgeTagCreateParams) => {
  const response = await request.post<ApiResponse<KnowledgeTag>>(
    '/admin/knowledge/tags',
    data
  )
  return response.data.data
}

export const updateTagApi = async (
  id: number,
  data: KnowledgeTagUpdateParams
) => {
  await request.put(`/admin/knowledge/tags/${id}`, data)
}

export const deleteTagApi = async (id: number) => {
  await request.delete(`/admin/knowledge/tags/${id}`)
}

// ==================== 图片上传 ====================

/**
 * 知识库图片上传（封面或正文插图）
 */
export const uploadKnowledgeImageApi = async (file: File): Promise<FileUploadResponse> => {
  const resp = await uploadFile('/admin/knowledge/upload', file, 'file')
  if (resp.code !== 200 || !resp.data) throw new Error(resp.message || '上传失败')
  return resp.data
}
