package com.ccs.ipc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ccs.ipc.common.exception.ApiException;
import com.ccs.ipc.common.response.ResultCode;
import com.ccs.ipc.dto.permissiondto.CreatePermissionRequest;
import com.ccs.ipc.dto.permissiondto.PermissionTreeNode;
import com.ccs.ipc.dto.permissiondto.SysPermissionResponse;
import com.ccs.ipc.dto.permissiondto.UpdatePermissionRequest;
import com.ccs.ipc.entity.SysPermission;
import com.ccs.ipc.mapper.SysPermissionMapper;
import com.ccs.ipc.service.ISysPermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 系统权限表 服务实现类
 * </p>
 *
 * @author WZH
 * @since 2026-01-19
 */
@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements ISysPermissionService {

    @Override
    public List<PermissionTreeNode> getPermissionTree(Byte permissionType) {
        LambdaQueryWrapper<SysPermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysPermission::getIsDeleted, 0);
        if (permissionType != null) {
            queryWrapper.eq(SysPermission::getPermissionType, permissionType);
        }
        queryWrapper.orderByAsc(SysPermission::getSort);

        List<SysPermission> allPermissions = this.list(queryWrapper);
        return buildTree(allPermissions, 0L);
    }

    @Override
    public SysPermissionResponse createPermission(CreatePermissionRequest request) {
        // 检查权限编码是否已存在
        LambdaQueryWrapper<SysPermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysPermission::getPermissionCode, request.getPermissionCode())
                .eq(SysPermission::getIsDeleted, 0);
        SysPermission existPermission = this.getOne(queryWrapper);
        if (existPermission != null) {
            throw new ApiException(ResultCode.PERMISSION_CODE_EXISTS.getMessage());
        }

        SysPermission permission = new SysPermission();
        permission.setPermissionCode(request.getPermissionCode());
        permission.setPermissionName(request.getPermissionName());
        permission.setPermissionType(request.getPermissionType());
        permission.setParentId(request.getParentId() != null ? request.getParentId() : 0L);
        permission.setSort(request.getSort() != null ? request.getSort() : 0);
        permission.setIsDeleted((byte) 0);

        this.save(permission);
        return convertToResponse(permission);
    }

    @Override
    public SysPermissionResponse updatePermission(Long id, UpdatePermissionRequest request) {
        SysPermission permission = this.getById(id);
        if (permission == null || (permission.getIsDeleted() != null && permission.getIsDeleted() == 1)) {
            throw new ApiException(ResultCode.PERMISSION_NOT_FOUNT.getMessage());
        }

        if (StringUtils.hasText(request.getPermissionName())) {
            permission.setPermissionName(request.getPermissionName());
        }
        if (request.getPermissionType() != null) {
            permission.setPermissionType(request.getPermissionType());
        }
        if (request.getParentId() != null) {
            permission.setParentId(request.getParentId());
        }
        if (request.getSort() != null) {
            permission.setSort(request.getSort());
        }

        this.updateById(permission);
        return convertToResponse(permission);
    }

    @Override
    public void deletePermission(Long id) {
        SysPermission permission = this.getById(id);
        if (permission == null || (permission.getIsDeleted() != null && permission.getIsDeleted() == 1)) {
            throw new ApiException(ResultCode.PERMISSION_NOT_FOUNT.getMessage());
        }
        permission.setIsDeleted((byte) 1);
        this.updateById(permission);
    }

    @Override
    public List<SysPermissionResponse> getPermissionList(Byte permissionType){
        LambdaQueryWrapper<SysPermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysPermission::getIsDeleted, 0);
        if (permissionType != null) {
            queryWrapper.eq(SysPermission::getPermissionType, permissionType);
        }
        queryWrapper.orderByAsc(SysPermission::getSort);

        List<SysPermission> permissions = this.list(queryWrapper);
        List<SysPermissionResponse> responses = new ArrayList<>();
        for (SysPermission permission : permissions) {
            responses.add(convertToResponse(permission));
        }

        return responses;
    }

    @Override
    public SysPermissionResponse getPermissionById(Long id) {
        SysPermission permission = this.getById(id);
        if (permission == null) {
            throw new ApiException(ResultCode.PERMISSION_NOT_FOUNT.getMessage());
        }
        return convertToResponse(permission);
    }

    /**
     * 构建权限树
     */
    private List<PermissionTreeNode> buildTree(List<SysPermission> permissions, Long parentId) {
        List<PermissionTreeNode> nodes = new ArrayList<>();

        for (SysPermission permission : permissions) {
            if (permission.getParentId() != null && permission.getParentId().equals(parentId)) {
                PermissionTreeNode node = new PermissionTreeNode();
                node.setId(permission.getId());
                node.setPermissionCode(permission.getPermissionCode());
                node.setPermissionName(permission.getPermissionName());
                node.setPermissionType(permission.getPermissionType());
                node.setParentId(permission.getParentId());
                node.setSort(permission.getSort());

                // 递归构建子节点
                List<PermissionTreeNode> children = buildTree(permissions, permission.getId());
                node.setChildren(children);

                nodes.add(node);
            }
        }

        return nodes;
    }

    private SysPermissionResponse convertToResponse(SysPermission  permission) {
        SysPermissionResponse response = new SysPermissionResponse();
        BeanUtils.copyProperties(permission, response);
        return response;
    }
}
