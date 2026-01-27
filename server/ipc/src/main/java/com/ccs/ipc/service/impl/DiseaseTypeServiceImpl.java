package com.ccs.ipc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ccs.ipc.dto.patientdto.DiseaseTypeResponse;
import com.ccs.ipc.entity.DiseaseType;
import com.ccs.ipc.mapper.DiseaseTypeMapper;
import com.ccs.ipc.service.IDiseaseTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public List<DiseaseTypeResponse> getAllDiseaseTypes() {
        LambdaQueryWrapper<DiseaseType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DiseaseType::getIsDeleted, 0)
                .orderByAsc(DiseaseType::getDiseaseName);

        List<DiseaseType> types = this.list(queryWrapper);

        return types.stream()
                .map(type -> {
                    DiseaseTypeResponse response = new DiseaseTypeResponse();
                    response.setId(type.getId());
                    response.setName(type.getDiseaseName());
                    response.setDescription(type.getDescription());
                    response.setCategory(type.getDiseaseCategory());
                    return response;
                })
                .collect(Collectors.toList());
    }
}
