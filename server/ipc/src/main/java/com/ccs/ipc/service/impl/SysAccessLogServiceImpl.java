package com.ccs.ipc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ccs.ipc.dto.logdto.AccessLogListRequest;
import com.ccs.ipc.dto.logdto.AccessLogListResponse;
import com.ccs.ipc.dto.logdto.AccessLogResponse;
import com.ccs.ipc.entity.SysAccessLog;
import com.ccs.ipc.mapper.SysAccessLogMapper;
import com.ccs.ipc.service.ISysAccessLogService;
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
 * 系统接口访问日志表 服务实现类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
@Slf4j
@Service
public class SysAccessLogServiceImpl extends ServiceImpl<SysAccessLogMapper, SysAccessLog> implements ISysAccessLogService {

    /**
     * 异步保存访问日志
     * 使用@Async注解实现真正的异步执行，避免日志IO拖慢接口响应
     *
     * @param accessLog 访问日志
     */
    @Async
    @Override
    public void saveAsync(SysAccessLog accessLog) {
        try {
            this.save(accessLog);
        } catch (Exception e) {
            log.error("异步保存访问日志失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 分页查询访问日志列表
     */
    @Override
    public AccessLogListResponse getAccessLogList(AccessLogListRequest request) {
        // 确保分页参数有效
        long current = request.getCurrent() != null && request.getCurrent() > 0 ? request.getCurrent() : 1L;
        long size = request.getSize() != null && request.getSize() > 0 ? request.getSize() : 10L;
        
        Page<SysAccessLog> page = new Page<>(current, size);
        LambdaQueryWrapper<SysAccessLog> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(request.getUsername())) {
            queryWrapper.like(SysAccessLog::getUsername, request.getUsername());
        }
        if (StringUtils.hasText(request.getRequestUrl())) {
            queryWrapper.like(SysAccessLog::getRequestUrl, request.getRequestUrl());
        }
        if (StringUtils.hasText(request.getRequestMethod())) {
            queryWrapper.eq(SysAccessLog::getRequestMethod, request.getRequestMethod());
        }
        if (request.getResponseStatus() != null) {
            queryWrapper.eq(SysAccessLog::getResponseStatus, request.getResponseStatus());
        }
        if (request.getStartTime() != null) {
            queryWrapper.ge(SysAccessLog::getCreateTime, request.getStartTime());
        }
        if (request.getEndTime() != null) {
            queryWrapper.le(SysAccessLog::getCreateTime, request.getEndTime());
        }
        queryWrapper.orderByDesc(SysAccessLog::getCreateTime);

        // 执行分页查询
        Page<SysAccessLog> result = this.page(page, queryWrapper);

        // 转换为Response
        AccessLogListResponse response = new AccessLogListResponse();
        response.setTotal(result.getTotal());
        response.setCurrent(result.getCurrent());
        response.setSize(result.getSize());

        List<AccessLogResponse> responseList = new ArrayList<>();
        for (SysAccessLog log : result.getRecords()) {
            responseList.add(convertToResponse(log));
        }
        response.setRecords(responseList);

        return response;
    }

    /**
     * 根据ID获取访问日志详情
     */
    @Override
    public AccessLogResponse getAccessLogById(Long id) {
        SysAccessLog log = this.getById(id);
        if (log == null) {
            return null;
        }
        return convertToResponse(log);
    }

    /**
     * 将实体转换为Response
     */
    private AccessLogResponse convertToResponse(SysAccessLog log) {
        AccessLogResponse response = new AccessLogResponse();
        BeanUtils.copyProperties(log, response);
        return response;
    }
}

