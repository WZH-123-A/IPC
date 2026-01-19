package com.ccs.ipc.service.impl;

import com.ccs.ipc.entity.ConsultationMessage;
import com.ccs.ipc.mapper.ConsultationMessageMapper;
import com.ccs.ipc.service.IConsultationMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 问诊消息记录表 服务实现类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
@Service
public class ConsultationMessageServiceImpl extends ServiceImpl<ConsultationMessageMapper, ConsultationMessage> implements IConsultationMessageService {

}
