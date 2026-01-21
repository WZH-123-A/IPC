package com.ccs.ipc.service.impl;

import com.ccs.ipc.entity.SysAccessLog;
import com.ccs.ipc.mapper.SysAccessLogMapper;
import com.ccs.ipc.service.ISysAccessLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
}

