package com.ccs.ipc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ccs.ipc.dto.statisticsdto.StatTypeSum;
import com.ccs.ipc.dto.statisticsdto.StatisticsDailyListRequest;
import com.ccs.ipc.dto.statisticsdto.StatisticsDailyListResponse;
import com.ccs.ipc.dto.statisticsdto.StatisticsDailyResponse;
import com.ccs.ipc.dto.statisticsdto.StatisticsDailySummaryResponse;
import com.ccs.ipc.entity.ConsultationSession;
import com.ccs.ipc.entity.SkinDiagnosisRecord;
import com.ccs.ipc.entity.StatisticsDaily;
import com.ccs.ipc.entity.SysUser;
import com.ccs.ipc.mapper.StatisticsDailyMapper;
import com.ccs.ipc.service.IConsultationSessionService;
import com.ccs.ipc.service.ISkinDiagnosisRecordService;
import com.ccs.ipc.service.IStatisticsDailyService;
import com.ccs.ipc.service.ISysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 每日统计数据表 服务实现类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
@Slf4j
@Service
public class StatisticsDailyServiceImpl extends ServiceImpl<StatisticsDailyMapper, StatisticsDaily> implements IStatisticsDailyService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private IConsultationSessionService consultationSessionService;
    @Autowired
    private ISkinDiagnosisRecordService skinDiagnosisRecordService;

    @Override
    public StatisticsDailyListResponse getList(StatisticsDailyListRequest request) {
        long current = request.getCurrent() != null && request.getCurrent() > 0 ? request.getCurrent() : 1L;
        long size = request.getSize() != null && request.getSize() > 0 ? request.getSize() : 10L;
        Page<StatisticsDaily> page = new Page<>(current, size);
        LambdaQueryWrapper<StatisticsDaily> q = new LambdaQueryWrapper<>();
        if (request.getStartDate() != null) {
            q.ge(StatisticsDaily::getStatDate, request.getStartDate());
        }
        if (request.getEndDate() != null) {
            q.le(StatisticsDaily::getStatDate, request.getEndDate());
        }
        if (StringUtils.hasText(request.getStatType())) {
            q.eq(StatisticsDaily::getStatType, request.getStatType());
        }
        q.orderByDesc(StatisticsDaily::getStatDate).orderByDesc(StatisticsDaily::getCreateTime);
        Page<StatisticsDaily> result = page(page, q);
        List<StatisticsDailyResponse> records = result.getRecords().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        StatisticsDailyListResponse response = new StatisticsDailyListResponse();
        response.setRecords(records);
        response.setTotal(result.getTotal());
        response.setCurrent(result.getCurrent());
        response.setSize(result.getSize());
        return response;
    }

    @Override
    public StatisticsDailySummaryResponse getSummary(LocalDate startDate, LocalDate endDate, String statType) {
        long totalRecords = count(new LambdaQueryWrapper<StatisticsDaily>()
                .ge(startDate != null, StatisticsDaily::getStatDate, startDate)
                .le(endDate != null, StatisticsDaily::getStatDate, endDate)
                .eq(StringUtils.hasText(statType), StatisticsDaily::getStatType, statType));
        List<StatTypeSum> sums = baseMapper.sumByType(startDate, endDate, statType);
        Map<String, Long> sumByType = sums == null ? new HashMap<>() : sums.stream()
                .filter(s -> s.getStatType() != null)
                .collect(Collectors.toMap(StatTypeSum::getStatType, StatTypeSum::getTotal, (a, b) -> a));
        StatisticsDailySummaryResponse response = new StatisticsDailySummaryResponse();
        response.setTotalRecords(totalRecords);
        response.setSumByType(sumByType);
        return response;
    }

    @Override
    public void recordDailyStats(LocalDate statDate) {
        LocalDateTime dayStart = statDate.atStartOfDay();
        LocalDateTime dayEnd = statDate.atTime(23, 59, 59, 999_999_999);

        long userRegisterCount = sysUserService.count(
                new LambdaQueryWrapper<SysUser>()
                        .ge(SysUser::getCreateTime, dayStart)
                        .le(SysUser::getCreateTime, dayEnd));
        long consultationCount = consultationSessionService.count(
                new LambdaQueryWrapper<ConsultationSession>()
                        .ge(ConsultationSession::getCreateTime, dayStart)
                        .le(ConsultationSession::getCreateTime, dayEnd));
        long diagnosisCount = skinDiagnosisRecordService.count(
                new LambdaQueryWrapper<SkinDiagnosisRecord>()
                        .ge(SkinDiagnosisRecord::getCreateTime, dayStart)
                        .le(SkinDiagnosisRecord::getCreateTime, dayEnd));
        long knowledgeViewCount = 0L; // 暂无知识浏览埋点，可后续从访问日志按 URL 统计

        remove(new LambdaQueryWrapper<StatisticsDaily>().eq(StatisticsDaily::getStatDate, statDate));

        saveRow(statDate, "user_register", userRegisterCount);
        saveRow(statDate, "consultation", consultationCount);
        saveRow(statDate, "diagnosis", diagnosisCount);
        saveRow(statDate, "knowledge_view", knowledgeViewCount);

        log.info("每日统计已采集: statDate={}, user_register={}, consultation={}, diagnosis={}, knowledge_view={}",
                statDate, userRegisterCount, consultationCount, diagnosisCount, knowledgeViewCount);
    }

    private void saveRow(LocalDate statDate, String statType, long statValue) {
        StatisticsDaily row = new StatisticsDaily();
        row.setStatDate(statDate);
        row.setStatType(statType);
        row.setStatValue(statValue);
        save(row);
    }

    private StatisticsDailyResponse toResponse(StatisticsDaily e) {
        StatisticsDailyResponse r = new StatisticsDailyResponse();
        r.setId(e.getId());
        r.setStatDate(e.getStatDate() == null ? null : e.getStatDate().format(DATE_FMT));
        r.setStatType(e.getStatType());
        r.setStatValue(e.getStatValue());
        r.setExtraData(e.getExtraData());
        r.setCreateTime(e.getCreateTime() == null ? null : e.getCreateTime().format(DATETIME_FMT));
        r.setUpdateTime(e.getUpdateTime() == null ? null : e.getUpdateTime().format(DATETIME_FMT));
        return r;
    }
}
