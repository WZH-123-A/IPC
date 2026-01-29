package com.ccs.ipc.dto.statisticsdto;

import lombok.Data;

import java.util.List;

/**
 * 每日统计列表响应
 *
 * @author WZH
 * @since 2026-01-29
 */
@Data
public class StatisticsDailyListResponse {

    private List<StatisticsDailyResponse> records;
    private Long total;
    private Long current;
    private Long size;
}
