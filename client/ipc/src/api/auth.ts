import request, { type ApiResponse } from './request'

// 登录参数接口
export interface LoginParams {
  username: string
  password: string
  rememberMe?: boolean
}

// 角色类型
export type UserRole = 'patient' | 'doctor' | 'admin'

// 登录响应数据接口（匹配后端 LoginResponse）
export interface LoginResponse {
  token: string
  userId: number
  username: string
  realName: string
  roles: UserRole[]
}

// 用户信息接口
export interface UserInfo {
  id: number
  userId: number
  username: string
  realName: string
  roles: UserRole[]
  email?: string
  avatar?: string
}

/**
 * 用户登录
 * @param params 登录参数
 * @returns 登录响应数据
 */
export const loginApi = async (params: LoginParams): Promise<LoginResponse> => {
  const response = await request.post<ApiResponse<LoginResponse>>('/auth/login', {
    username: params.username,
    password: params.password,
  })
  // 响应拦截器已经处理了 code 检查，这里直接返回 data
  return response.data.data
}

/**
 * 用户登出
 */
export const logoutApi = async (): Promise<void> => {
  await request.post<ApiResponse>('/auth/logout')
}

/**
 * 获取当前用户信息
 * @returns 用户信息
 */
export const getUserInfoApi = async (): Promise<UserInfo> => {
  const response = await request.get<ApiResponse<UserInfo>>('/auth/userinfo')
  return response.data.data
}

/**
 * 刷新 token
 * @returns 新的 token
 */
export const refreshTokenApi = async (): Promise<string> => {
  const response = await request.post<ApiResponse<{ token: string }>>('/auth/refresh')
  return response.data.data.token
}
