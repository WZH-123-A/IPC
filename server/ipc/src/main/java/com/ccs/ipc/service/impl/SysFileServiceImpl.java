package com.ccs.ipc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ccs.ipc.dto.admindto.SysFileListRequest;
import com.ccs.ipc.dto.admindto.SysFileListResponse;
import com.ccs.ipc.dto.admindto.SysFileResponse;
import com.ccs.ipc.entity.SysFile;
import com.ccs.ipc.mapper.SysFileMapper;
import com.ccs.ipc.service.ISysFileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 文件上传记录表 服务实现类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
@Service
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, SysFile> implements ISysFileService {

    @Override
    public SysFileListResponse getFileList(SysFileListRequest request) {
        long current = request.getCurrent() != null && request.getCurrent() > 0 ? request.getCurrent() : 1L;
        long size = request.getSize() != null && request.getSize() > 0 ? request.getSize() : 10L;

        Page<SysFile> page = new Page<>(current, size);
        LambdaQueryWrapper<SysFile> qw = new LambdaQueryWrapper<>();
        qw.eq(SysFile::getIsDeleted, 0);

        if (StringUtils.hasText(request.getFileName())) {
            qw.like(SysFile::getFileName, request.getFileName());
        }
        if (StringUtils.hasText(request.getFileType())) {
            qw.eq(SysFile::getFileType, request.getFileType());
        }
        if (StringUtils.hasText(request.getBusinessType())) {
            qw.eq(SysFile::getBusinessType, request.getBusinessType());
        }
        qw.orderByDesc(SysFile::getCreateTime);

        Page<SysFile> result = this.page(page, qw);
        List<SysFileResponse> records = result.getRecords().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        SysFileListResponse response = new SysFileListResponse();
        response.setRecords(records);
        response.setTotal(result.getTotal());
        response.setCurrent(result.getCurrent());
        response.setSize(result.getSize());
        return response;
    }

    @Override
    public SysFileResponse getFileById(Long id) {
        SysFile entity = this.getById(id);
        if (entity == null || entity.getIsDeleted() != null && entity.getIsDeleted() == 1) {
            return null;
        }
        return toResponse(entity);
    }

    private SysFileResponse toResponse(SysFile entity) {
        SysFileResponse dto = new SysFileResponse();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
