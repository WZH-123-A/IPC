package com.ccs.ipc.service.impl;

import com.ccs.ipc.entity.PatientInfo;
import com.ccs.ipc.mapper.PatientInfoMapper;
import com.ccs.ipc.service.IPatientInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 患者扩展信息表 服务实现类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
@Service
public class PatientInfoServiceImpl extends ServiceImpl<PatientInfoMapper, PatientInfo> implements IPatientInfoService {

}
