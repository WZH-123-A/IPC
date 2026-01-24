package com.ccs.ipc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ccs.ipc.dto.logdto.OperationLogListRequest;
import com.ccs.ipc.dto.logdto.OperationLogListResponse;
import com.ccs.ipc.dto.logdto.OperationLogResponse;
import com.ccs.ipc.entity.SysOperationLog;
import com.ccs.ipc.mapper.SysOperationLogMapper;
import com.ccs.ipc.service.ISysOperationLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 系统操作日志表 服务实现类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
@Slf4j
@Service
public class SysOperationLogServiceImpl extends ServiceImpl<SysOperationLogMapper, SysOperationLog> implements ISysOperationLogService {

    /**
     * 异步保存操作日志
     * 使用@Async注解实现真正的异步执行，避免日志IO和数据库慢查询拖慢接口响应
     *
     * @param operationLog 操作日志
     */
    @Async
    @Override
    public void saveAsync(SysOperationLog operationLog) {
        try {
            this.save(operationLog);
        } catch (Exception e) {
            log.error("异步保存操作日志失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 分页查询操作日志列表
     */
    @Override
    public OperationLogListResponse getOperationLogList(OperationLogListRequest request) {
        // 确保分页参数有效
        long current = request.getCurrent() != null && request.getCurrent() > 0 ? request.getCurrent() : 1L;
        long size = request.getSize() != null && request.getSize() > 0 ? request.getSize() : 10L;
        
        Page<SysOperationLog> page = new Page<>(current, size);
        LambdaQueryWrapper<SysOperationLog> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(request.getUsername())) {
            queryWrapper.like(SysOperationLog::getUsername, request.getUsername());
        }
        if (StringUtils.hasText(request.getOperationType())) {
            queryWrapper.eq(SysOperationLog::getOperationType, request.getOperationType());
        }
        if (StringUtils.hasText(request.getOperationModule())) {
            queryWrapper.eq(SysOperationLog::getOperationModule, request.getOperationModule());
        }
        if (request.getStatus() != null) {
            queryWrapper.eq(SysOperationLog::getStatus, request.getStatus());
        }
        if (request.getStartTime() != null) {
            queryWrapper.ge(SysOperationLog::getCreateTime, request.getStartTime());
        }
        if (request.getEndTime() != null) {
            queryWrapper.le(SysOperationLog::getCreateTime, request.getEndTime());
        }
        queryWrapper.orderByDesc(SysOperationLog::getCreateTime);

        // 执行分页查询
        Page<SysOperationLog> result = this.page(page, queryWrapper);

        // 转换为Response
        OperationLogListResponse response = new OperationLogListResponse();
        response.setTotal(result.getTotal());
        response.setCurrent(result.getCurrent());
        response.setSize(result.getSize());

        List<OperationLogResponse> responseList = new ArrayList<>();
        for (SysOperationLog log : result.getRecords()) {
            responseList.add(convertToResponse(log));
        }
        response.setRecords(responseList);

        return response;
    }

    /**
     * 根据ID获取操作日志详情
     */
    @Override
    public OperationLogResponse getOperationLogById(Long id) {
        SysOperationLog log = this.getById(id);
        if (log == null) {
            return null;
        }
        return convertToResponse(log);
    }

    /**
     * 将实体转换为Response
     */
    private OperationLogResponse convertToResponse(SysOperationLog log) {
        OperationLogResponse response = new OperationLogResponse();
        BeanUtils.copyProperties(log, response);
        return response;
    }
}
