package com.ccs.ipc.dto.statisticsdto;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 每日统计汇总（按类型汇总、总记录数等）
 *
 * @author WZH
 * @since 2026-01-29
 */
@Data
public class StatisticsDailySummaryResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 总记录数 */
    private Long totalRecords;

    /** 各统计类型的数值之和：statType -> sum(statValue) */
    private Map<String, Long> sumByType;
}
