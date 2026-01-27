package com.ccs.ipc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ccs.ipc.dto.patientdto.DiagnosisResultResponse;
import com.ccs.ipc.entity.DiagnosisResult;
import com.ccs.ipc.entity.DiseaseType;
import com.ccs.ipc.entity.SkinDiagnosisRecord;
import com.ccs.ipc.mapper.DiagnosisResultMapper;
import com.ccs.ipc.service.IDiagnosisResultService;
import com.ccs.ipc.service.IDiseaseTypeService;
import com.ccs.ipc.service.ISkinDiagnosisRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 识别结果详情表 服务实现类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
@Service
public class DiagnosisResultServiceImpl extends ServiceImpl<DiagnosisResultMapper, DiagnosisResult> implements IDiagnosisResultService {

    @Autowired
    private ISkinDiagnosisRecordService skinDiagnosisRecordService;

    @Autowired
    private IDiseaseTypeService diseaseTypeService;

    @Override
    public List<DiagnosisResultResponse> getRecordResults(Long recordId, Long userId) {
        // 验证记录归属
        SkinDiagnosisRecord record = skinDiagnosisRecordService.getById(recordId);
        if (record == null || record.getIsDeleted() == 1) {
            throw new RuntimeException("诊断记录不存在");
        }
        if (!record.getUserId().equals(userId)) {
            throw new RuntimeException("无权访问此诊断记录");
        }

        LambdaQueryWrapper<DiagnosisResult> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DiagnosisResult::getRecordId, recordId)
                .orderByAsc(DiagnosisResult::getRank);

        List<DiagnosisResult> results = this.list(queryWrapper);

        List<DiagnosisResultResponse> responseList = new ArrayList<>();
        for (DiagnosisResult result : results) {
            DiagnosisResultResponse response = new DiagnosisResultResponse();
            BeanUtils.copyProperties(result, response);

            // 查询疾病名称
            DiseaseType diseaseType = diseaseTypeService.getById(result.getDiseaseId());
            if (diseaseType != null) {
                response.setDiseaseName(diseaseType.getDiseaseName());
            }

            responseList.add(response);
        }

        return responseList;
    }
}
