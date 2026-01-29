package com.ccs.ipc.common.enums.log;

import lombok.Getter;

/**
 * 每日统计管理模块（StatisticsDailyController）操作方法
 *
 * @author WZH
 * @since 2026-01-29
 */
@Getter
public enum StatisticsDailyOperation implements IOperationType {
    LIST("查询每日统计列表"),
    SUMMARY("查询每日统计汇总"),
    COLLECT("触发统计采集");

    /** 供 @Log 注解使用的编译期常量 */
    public static final class C {
        public static final String LIST = "查询每日统计列表";
        public static final String SUMMARY = "查询每日统计汇总";
        public static final String COLLECT = "触发统计采集";
    }

    private final String value;

    StatisticsDailyOperation(String value) {
        this.value = value;
    }
}
