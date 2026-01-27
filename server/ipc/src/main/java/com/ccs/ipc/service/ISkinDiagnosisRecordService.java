package com.ccs.ipc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ccs.ipc.dto.patientdto.DiagnosisRecordListRequest;
import com.ccs.ipc.dto.patientdto.DiagnosisRecordListResponse;
import com.ccs.ipc.dto.patientdto.DiagnosisRecordResponse;
import com.ccs.ipc.entity.SkinDiagnosisRecord;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 皮肤病识别记录表 服务类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
public interface ISkinDiagnosisRecordService extends IService<SkinDiagnosisRecord> {

    /**
     * 上传诊断图片并创建记录
     *
     * @param userId    患者ID
     * @param image     图片文件
     * @param bodyPart  身体部位
     * @return 诊断记录响应
     */
    DiagnosisRecordResponse uploadAndCreateRecord(Long userId, MultipartFile image, String bodyPart);

    /**
     * 获取患者的诊断记录列表
     *
     * @param userId  患者ID
     * @param request 查询请求
     * @return 诊断记录列表响应
     */
    DiagnosisRecordListResponse getPatientRecords(Long userId, DiagnosisRecordListRequest request);

    /**
     * 获取诊断记录详情（验证权限）
     *
     * @param recordId 记录ID
     * @param userId   患者ID
     * @return 诊断记录响应
     */
    DiagnosisRecordResponse getRecordDetail(Long recordId, Long userId);
}
