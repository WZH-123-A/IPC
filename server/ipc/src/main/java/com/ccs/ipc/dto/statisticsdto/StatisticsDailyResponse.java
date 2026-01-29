package com.ccs.ipc.dto.statisticsdto;

import lombok.Data;

import java.io.Serializable;

/**
 * 每日统计单项响应
 *
 * @author WZH
 * @since 2026-01-29
 */
@Data
public class StatisticsDailyResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    /** 统计日期 yyyy-MM-dd */
    private String statDate;
    private String statType;
    private Long statValue;
    private String extraData;
    private String createTime;
    private String updateTime;
}
