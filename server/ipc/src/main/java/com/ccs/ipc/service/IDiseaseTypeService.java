package com.ccs.ipc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ccs.ipc.dto.patientdto.DiseaseTypeResponse;
import com.ccs.ipc.entity.DiseaseType;

import java.util.List;

/**
 * <p>
 * 疾病类型表 服务类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
public interface IDiseaseTypeService extends IService<DiseaseType> {

    /**
     * 获取所有疾病类型列表
     *
     * @return 疾病类型列表
     */
    List<DiseaseTypeResponse> getAllDiseaseTypes();
}
