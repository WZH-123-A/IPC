import request, { type ApiResponse } from '../request'

/** 在线人数接口返回 */
export interface OnlineCountResponse {
  onlineCount: number
}

/**
 * 获取当前在线人数（基于 WebSocket 连接的去重用户数）
 */
export function getOnlineCountApi(): Promise<OnlineCountResponse | undefined> {
  return request
    .get<ApiResponse<OnlineCountResponse>>('/admin/home/online-count')
    .then((res) => res.data?.data)
}
