import request, { type ApiResponse } from '../request'

// ==================== 每日统计 ====================

export interface StatisticsDailyItem {
  id: number
  statDate: string
  statType: string
  statValue: number
  extraData?: string
  createTime: string
  updateTime?: string
}

export interface StatisticsDailyListResult {
  records: StatisticsDailyItem[]
  total: number
  current: number
  size: number
}

export interface StatisticsDailySummaryResult {
  totalRecords: number
  sumByType: Record<string, number>
}

export interface StatisticsDailyListParams {
  current?: number
  size?: number
  startDate?: string
  endDate?: string
  statType?: string
}

export interface StatisticsDailySummaryParams {
  startDate?: string
  endDate?: string
  statType?: string
}

/**
 * 获取每日统计列表（分页）
 */
export function getStatisticsDailyListApi(
  params: StatisticsDailyListParams
): Promise<StatisticsDailyListResult | undefined> {
  return request
    .get<ApiResponse<StatisticsDailyListResult>>('/admin/statistics-daily/list', { params })
    .then((res) => res.data?.data)
}

/**
 * 获取每日统计汇总（按类型汇总、总记录数）
 */
export function getStatisticsDailySummaryApi(
  params?: StatisticsDailySummaryParams
): Promise<StatisticsDailySummaryResult | undefined> {
  return request
    .get<ApiResponse<StatisticsDailySummaryResult>>('/admin/statistics-daily/summary', { params })
    .then((res) => res.data?.data)
}

/**
 * 触发指定日期的统计采集（用于补采或测试，不传 date 则采集昨日）
 */
export function collectStatisticsDailyApi(date?: string): Promise<void> {
  return request
    .post<ApiResponse<void>>('/admin/statistics-daily/collect', null, {
      params: date ? { date } : undefined,
    })
    .then(() => undefined)
}
