package com.ccs.ipc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ccs.ipc.dto.patientdto.DiagnosisRecordListRequest;
import com.ccs.ipc.dto.patientdto.DiagnosisRecordListResponse;
import com.ccs.ipc.dto.patientdto.DiagnosisRecordResponse;
import com.ccs.ipc.entity.SkinDiagnosisRecord;
import com.ccs.ipc.mapper.SkinDiagnosisRecordMapper;
import com.ccs.ipc.service.ISkinDiagnosisRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <p>
 * 皮肤病识别记录表 服务实现类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
@Service
public class SkinDiagnosisRecordServiceImpl extends ServiceImpl<SkinDiagnosisRecordMapper, SkinDiagnosisRecord> implements ISkinDiagnosisRecordService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DiagnosisRecordResponse uploadAndCreateRecord(Long userId, MultipartFile image, String bodyPart) {
        if (image == null || image.isEmpty()) {
            throw new RuntimeException("请选择要上传的图片");
        }

        // TODO: 上传文件到文件服务，获取文件URL
        // 这里暂时使用占位符
        String imageUrl = "/uploads/diagnosis/" + UUID.randomUUID().toString() + ".jpg";
        String imageName = image.getOriginalFilename();

        SkinDiagnosisRecord record = new SkinDiagnosisRecord();
        record.setUserId(userId);
        record.setImageUrl(imageUrl);
        record.setImageName(imageName);
        record.setBodyPart(bodyPart);
        record.setModelVersion("ResNet50");
        record.setStatus((byte) 0); // 识别中
        record.setIsDeleted((byte) 0);

        this.save(record);

        // TODO: 调用AI诊断服务进行识别
        // 这里暂时设置为识别成功
        record.setStatus((byte) 1);
        this.updateById(record);

        return convertToResponse(record);
    }

    @Override
    public DiagnosisRecordListResponse getPatientRecords(Long userId, DiagnosisRecordListRequest request) {
        Page<SkinDiagnosisRecord> page = new Page<>(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<SkinDiagnosisRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkinDiagnosisRecord::getUserId, userId)
                .eq(SkinDiagnosisRecord::getIsDeleted, 0)
                .orderByDesc(SkinDiagnosisRecord::getCreateTime);

        Page<SkinDiagnosisRecord> result = this.page(page, queryWrapper);

        DiagnosisRecordListResponse response = new DiagnosisRecordListResponse();
        response.setTotal(result.getTotal());
        response.setCurrent(result.getCurrent());
        response.setSize(result.getSize());

        List<DiagnosisRecordResponse> responseList = result.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        response.setRecords(responseList);

        return response;
    }

    @Override
    public DiagnosisRecordResponse getRecordDetail(Long recordId, Long userId) {
        SkinDiagnosisRecord record = this.getById(recordId);
        if (record == null || record.getIsDeleted() == 1) {
            throw new RuntimeException("诊断记录不存在");
        }

        if (!record.getUserId().equals(userId)) {
            throw new RuntimeException("无权访问此诊断记录");
        }

        return convertToResponse(record);
    }

    private DiagnosisRecordResponse convertToResponse(SkinDiagnosisRecord record) {
        DiagnosisRecordResponse response = new DiagnosisRecordResponse();
        BeanUtils.copyProperties(record, response);
        return response;
    }
}
