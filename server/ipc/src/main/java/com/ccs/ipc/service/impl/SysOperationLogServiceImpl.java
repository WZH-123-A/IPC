package com.ccs.ipc.service.impl;

import com.ccs.ipc.entity.SysOperationLog;
import com.ccs.ipc.mapper.SysOperationLogMapper;
import com.ccs.ipc.service.ISysOperationLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
}
