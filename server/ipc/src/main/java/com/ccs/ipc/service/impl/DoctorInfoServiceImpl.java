package com.ccs.ipc.service.impl;

import com.ccs.ipc.entity.DoctorInfo;
import com.ccs.ipc.mapper.DoctorInfoMapper;
import com.ccs.ipc.service.IDoctorInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 医生扩展信息表 服务实现类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
@Service
public class DoctorInfoServiceImpl extends ServiceImpl<DoctorInfoMapper, DoctorInfo> implements IDoctorInfoService {

}
