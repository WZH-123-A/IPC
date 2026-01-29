package com.ccs.ipc.common.enums.log;

import lombok.Getter;

/**
 * 每日统计管理模块（StatisticsDailyController）操作模块
 *
 * @author WZH
 * @since 2026-01-29
 */
@Getter
public enum StatisticsDailyModule implements IOperationModule {
    STATISTICS_DAILY("每日统计管理");

    /** 供 @Log 注解使用的编译期常量 */
    public static final class C {
        public static final String STATISTICS_DAILY = "每日统计管理";
    }

    private final String value;

    StatisticsDailyModule(String value) {
        this.value = value;
    }
}
