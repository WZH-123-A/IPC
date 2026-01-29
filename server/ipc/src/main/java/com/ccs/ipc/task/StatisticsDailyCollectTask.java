package com.ccs.ipc.task;

import com.ccs.ipc.service.IStatisticsDailyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 每日统计采集定时任务：按日汇总业务数据并写入 statistics_daily
 *
 * @author WZH
 * @since 2026-01-29
 */
@Slf4j
@Component
public class StatisticsDailyCollectTask {

    @Autowired
    private IStatisticsDailyService statisticsDailyService;

    /**
     * 每日 00:10 执行，采集「昨日」的统计数据
     */
    @Scheduled(cron = "0 10 0 * * ?")
    public void collectYesterday() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        try {
            statisticsDailyService.recordDailyStats(yesterday);
        } catch (Exception e) {
            log.error("每日统计采集失败: statDate={}", yesterday, e);
        }
    }
}
