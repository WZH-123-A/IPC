package com.ccs.ipc.service;

import com.ccs.ipc.dto.statisticsdto.StatisticsDailyListRequest;
import com.ccs.ipc.dto.statisticsdto.StatisticsDailyListResponse;
import com.ccs.ipc.dto.statisticsdto.StatisticsDailySummaryResponse;
import com.ccs.ipc.entity.StatisticsDaily;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDate;

/**
 * <p>
 * 每日统计数据表 服务类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
public interface IStatisticsDailyService extends IService<StatisticsDaily> {

    /**
     * 分页查询每日统计列表
     */
    StatisticsDailyListResponse getList(StatisticsDailyListRequest request);

    /**
     * 按日期范围与类型汇总
     */
    StatisticsDailySummaryResponse getSummary(LocalDate startDate, LocalDate endDate, String statType);

    /**
     * 采集指定日期的统计数据并写入 statistics_daily（从业务表按日汇总）
     */
    void recordDailyStats(LocalDate statDate);
}
