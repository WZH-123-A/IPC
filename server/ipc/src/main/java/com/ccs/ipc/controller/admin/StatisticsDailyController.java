package com.ccs.ipc.controller.admin;

import com.ccs.ipc.common.annotation.Log;
import com.ccs.ipc.common.annotation.RequirePermission;
import com.ccs.ipc.common.enums.log.StatisticsDailyModule;
import com.ccs.ipc.common.enums.log.StatisticsDailyOperation;
import com.ccs.ipc.common.response.Response;
import com.ccs.ipc.dto.statisticsdto.StatisticsDailyListRequest;
import com.ccs.ipc.dto.statisticsdto.StatisticsDailyListResponse;
import com.ccs.ipc.dto.statisticsdto.StatisticsDailySummaryResponse;
import com.ccs.ipc.service.IStatisticsDailyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;

/**
 * 每日统计管理控制器
 *
 * @author WZH
 * @since 2026-01-29
 */
@RestController
@RequestMapping("/api/admin/statistics-daily")
public class StatisticsDailyController {

    @Autowired
    private IStatisticsDailyService statisticsDailyService;

    /**
     * 分页查询每日统计列表
     */
    @GetMapping("/list")
    @RequirePermission("admin:api:statistics-daily:list")
    @Log(operationType = StatisticsDailyOperation.C.LIST, operationModule = StatisticsDailyModule.C.STATISTICS_DAILY, operationDesc = "查询每日统计列表")
    public Response<StatisticsDailyListResponse> getList(StatisticsDailyListRequest request) {
        StatisticsDailyListResponse response = statisticsDailyService.getList(request);
        return Response.success(response);
    }

    /**
     * 按日期范围与类型汇总
     */
    @GetMapping("/summary")
    @RequirePermission("admin:api:statistics-daily:list")
    @Log(operationType = StatisticsDailyOperation.C.SUMMARY, operationModule = StatisticsDailyModule.C.STATISTICS_DAILY, operationDesc = "查询每日统计汇总")
    public Response<StatisticsDailySummaryResponse> getSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DATE) LocalDate endDate,
            @RequestParam(required = false) String statType) {
        StatisticsDailySummaryResponse response = statisticsDailyService.getSummary(startDate, endDate, statType);
        return Response.success(response);
    }

    /**
     * 手动触发指定日期的统计采集（用于补采或测试）
     *
     * @param date 统计日期，不传则采集昨日
     */
    @PostMapping("/collect")
    @RequirePermission("admin:api:statistics-daily:list")
    @Log(operationType = StatisticsDailyOperation.C.COLLECT, operationModule = StatisticsDailyModule.C.STATISTICS_DAILY, operationDesc = "触发统计采集")
    public Response<Void> collect(
            @RequestParam(required = false) @DateTimeFormat(iso = DATE) LocalDate date) {
        LocalDate target = date != null ? date : LocalDate.now().minusDays(1);
        statisticsDailyService.recordDailyStats(target);
        return Response.success();
    }
}
