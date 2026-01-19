package com.ccs.ipc.service.impl;

import com.ccs.ipc.entity.DiseaseType;
import com.ccs.ipc.mapper.DiseaseTypeMapper;
import com.ccs.ipc.service.IDiseaseTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 疾病类型表 服务实现类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
@Service
public class DiseaseTypeServiceImpl extends ServiceImpl<DiseaseTypeMapper, DiseaseType> implements IDiseaseTypeService {

}
