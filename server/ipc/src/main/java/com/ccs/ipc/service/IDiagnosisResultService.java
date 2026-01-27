package com.ccs.ipc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ccs.ipc.dto.patientdto.DiagnosisResultResponse;
import com.ccs.ipc.entity.DiagnosisResult;

import java.util.List;

/**
 * <p>
 * 识别结果详情表 服务类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
public interface IDiagnosisResultService extends IService<DiagnosisResult> {

    /**
     * 获取诊断结果列表（验证记录权限）
     *
     * @param recordId 记录ID
     * @param userId   患者ID
     * @return 诊断结果列表
     */
    List<DiagnosisResultResponse> getRecordResults(Long recordId, Long userId);
}
