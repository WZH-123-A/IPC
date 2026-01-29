package com.ccs.ipc.dto.statisticsdto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 每日统计列表查询请求
 *
 * @author WZH
 * @since 2026-01-29
 */
@Data
public class StatisticsDailyListRequest {

    /** 当前页码，默认 1 */
    private Integer current = 1;

    /** 每页大小，默认 10 */
    private Integer size = 10;

    /** 统计日期起 */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    /** 统计日期止 */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    /** 统计类型（如 user_register / consultation / diagnosis / knowledge_view） */
    private String statType;
}
