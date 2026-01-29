package com.ccs.ipc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ccs.ipc.dto.statisticsdto.StatTypeSum;
import com.ccs.ipc.entity.StatisticsDaily;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 每日统计数据表 Mapper 接口
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
public interface StatisticsDailyMapper extends BaseMapper<StatisticsDaily> {

    /**
     * 按统计类型汇总数值（指定日期范围内）
     */
    List<StatTypeSum> sumByType(@Param("startDate") LocalDate startDate,
                                @Param("endDate") LocalDate endDate,
                                @Param("statType") String statType);
}
