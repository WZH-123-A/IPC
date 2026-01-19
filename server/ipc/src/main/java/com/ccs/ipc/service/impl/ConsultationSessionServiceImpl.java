package com.ccs.ipc.service.impl;

import com.ccs.ipc.entity.ConsultationSession;
import com.ccs.ipc.mapper.ConsultationSessionMapper;
import com.ccs.ipc.service.IConsultationSessionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 在线问诊会话表 服务实现类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
@Service
public class ConsultationSessionServiceImpl extends ServiceImpl<ConsultationSessionMapper, ConsultationSession> implements IConsultationSessionService {

}
