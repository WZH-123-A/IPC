package com.ccs.ipc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ccs.ipc.common.exception.ApiException;
import com.ccs.ipc.common.response.ResultCode;
import com.ccs.ipc.dto.permissiondto.CreatePermissionRequest;
import com.ccs.ipc.dto.permissiondto.PermissionTreeNode;
import com.ccs.ipc.dto.permissiondto.SysPermissionResponse;
import com.ccs.ipc.dto.permissiondto.UpdatePermissionRequest;
import com.ccs.ipc.entity.SysPermission;
import com.ccs.ipc.entity.SysRolePermission;
import com.ccs.ipc.entity.SysUserRole;
import com.ccs.ipc.mapper.SysPermissionMapper;
import com.ccs.ipc.service.ISysPermissionService;
import com.ccs.ipc.service.ISysRolePermissionService;
import com.ccs.ipc.service.ISysUserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Autowired
    private ISysUserRoleService sysUserRoleService;

    @Autowired
    private ISysRolePermissionService sysRolePermissionService;

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

        Long parentId = request.getParentId() != null ? request.getParentId() : 0L;
        Integer sort = request.getSort() != null ? request.getSort() : 0;
        
        // 如果指定的排序值在当前分组内已存在，需要调整排序
        adjustSortForInsert(parentId, sort);

        SysPermission permission = new SysPermission();
        permission.setPermissionCode(request.getPermissionCode());
        permission.setPermissionName(request.getPermissionName());
        permission.setPermissionType(request.getPermissionType());
        permission.setParentId(parentId);
        permission.setSort(sort);
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

        Long oldParentId = permission.getParentId();
        Integer oldSort = permission.getSort();
        Long newParentId = request.getParentId() != null ? request.getParentId() : oldParentId;
        Integer newSort = request.getSort() != null ? request.getSort() : oldSort;

        // 如果修改了父权限ID或排序值，需要调整排序
        if (!oldParentId.equals(newParentId) || !oldSort.equals(newSort)) {
            // 先恢复旧位置的排序（如果父权限改变，需要从旧分组中移除）
            if (!oldParentId.equals(newParentId)) {
                adjustSortForDelete(oldParentId, oldSort);
            } else if (!oldSort.equals(newSort)) {
                // 同一分组内调整排序
                adjustSortForUpdate(oldParentId, oldSort, newSort, id);
            }
            
            // 调整新位置的排序
            adjustSortForInsert(newParentId, newSort);
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
        
        Long parentId = permission.getParentId();
        Integer sort = permission.getSort();
        
        // 逻辑删除
        permission.setIsDeleted((byte) 1);
        this.updateById(permission);
        
        // 调整排序：将 > sort 的权限排序值都 -1
        adjustSortForDelete(parentId, sort);
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

    /**
     * 调整排序：在指定位置插入新权限时，将 >= sort 的权限排序值都 +1
     *
     * @param parentId 父权限ID
     * @param sort     新的排序值
     */
    private void adjustSortForInsert(Long parentId, Integer sort) {
        LambdaQueryWrapper<SysPermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysPermission::getParentId, parentId)
                .ge(SysPermission::getSort, sort)
                .eq(SysPermission::getIsDeleted, 0);
        
        List<SysPermission> permissions = this.list(queryWrapper);
        if (!permissions.isEmpty()) {
            for (SysPermission p : permissions) {
                p.setSort(p.getSort() + 1);
                this.updateById(p);
            }
        }
    }

    /**
     * 调整排序：删除权限时，将 > sort 的权限排序值都 -1
     *
     * @param parentId 父权限ID
     * @param sort     被删除的排序值
     */
    private void adjustSortForDelete(Long parentId, Integer sort) {
        LambdaQueryWrapper<SysPermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysPermission::getParentId, parentId)
                .gt(SysPermission::getSort, sort)
                .eq(SysPermission::getIsDeleted, 0);
        
        List<SysPermission> permissions = this.list(queryWrapper);
        if (!permissions.isEmpty()) {
            for (SysPermission p : permissions) {
                p.setSort(p.getSort() - 1);
                this.updateById(p);
            }
        }
    }

    /**
     * 调整排序：在同一分组内更新排序时，调整其他权限的排序值
     *
     * @param parentId 父权限ID
     * @param oldSort  旧的排序值
     * @param newSort  新的排序值
     * @param excludeId 排除的权限ID（当前更新的权限）
     */
    private void adjustSortForUpdate(Long parentId, Integer oldSort, Integer newSort, Long excludeId) {
        if (oldSort.equals(newSort)) {
            return;
        }

        if (newSort > oldSort) {
            // 向后移动：将 (oldSort, newSort] 范围内的权限排序值 -1
            LambdaQueryWrapper<SysPermission> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysPermission::getParentId, parentId)
                    .gt(SysPermission::getSort, oldSort)
                    .le(SysPermission::getSort, newSort)
                    .ne(SysPermission::getId, excludeId)
                    .eq(SysPermission::getIsDeleted, 0);
            
            List<SysPermission> permissions = this.list(queryWrapper);
            if (!permissions.isEmpty()) {
                for (SysPermission p : permissions) {
                    p.setSort(p.getSort() - 1);
                    this.updateById(p);
                }
            }
        } else {
            // 向前移动：将 [newSort, oldSort) 范围内的权限排序值 +1
            LambdaQueryWrapper<SysPermission> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysPermission::getParentId, parentId)
                    .ge(SysPermission::getSort, newSort)
                    .lt(SysPermission::getSort, oldSort)
                    .ne(SysPermission::getId, excludeId)
                    .eq(SysPermission::getIsDeleted, 0);
            
            List<SysPermission> permissions = this.list(queryWrapper);
            if (!permissions.isEmpty()) {
                for (SysPermission p : permissions) {
                    p.setSort(p.getSort() + 1);
                    this.updateById(p);
                }
            }
        }
    }
    @Override
    public List<PermissionTreeNode> getUserPermissionTree(Long userId){
        Set<Long> userPermissionIds = getUserPermissionIds(userId);

        if (userPermissionIds.isEmpty()) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<SysPermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysPermission::getId, userPermissionIds)
                .eq(SysPermission::getIsDeleted, 0);
        queryWrapper.orderByAsc(SysPermission::getSort);
        List<SysPermission> permissions = this.list(queryWrapper);

        return buildTree(permissions, 0L);
    }

    @Override
    public List<PermissionTreeNode> getUserMenuTree(Long userId) {
        // 1. 获取用户拥有的所有权限ID
        Set<Long> userPermissionIds = getUserPermissionIds(userId);
        
        if (userPermissionIds.isEmpty()) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<SysPermission> menuAndButtonQueryWrapper = new LambdaQueryWrapper<>();
        menuAndButtonQueryWrapper.in(SysPermission::getPermissionType, 1, 2)
                .eq(SysPermission::getIsDeleted, 0)
                .in(SysPermission::getId, userPermissionIds);
        menuAndButtonQueryWrapper.orderByAsc(SysPermission::getSort);
        List<SysPermission> menuAndButtonPermissions = this.list(menuAndButtonQueryWrapper);
        
        // 5. 构建权限树（包含按钮权限作为子节点）
        return buildTree(menuAndButtonPermissions, 248L);
    }

    @Override
    public List<String> getUserButtonPermissions(Long userId) {
        // 1. 获取用户拥有的所有权限ID
        Set<Long> userPermissionIds = getUserPermissionIds(userId);
        
        if (userPermissionIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 查询所有按钮权限（类型为2）
        LambdaQueryWrapper<SysPermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysPermission::getPermissionType, 2)
                .eq(SysPermission::getIsDeleted, 0)
                .in(SysPermission::getId, userPermissionIds);
        queryWrapper.orderByAsc(SysPermission::getSort);

        List<SysPermission> buttonPermissions = this.list(queryWrapper);
        
        // 3. 提取权限编码
        return buttonPermissions.stream()
                .map(SysPermission::getPermissionCode)
                .collect(Collectors.toList());
    }

    /**
     * 获取用户拥有的所有权限ID（通过用户 -> 角色 -> 权限的关联关系）
     *
     * @param userId 用户ID
     * @return 权限ID集合
     */
    private Set<Long> getUserPermissionIds(Long userId) {
        // 1. 查询用户角色关联
        LambdaQueryWrapper<SysUserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> userRoles = sysUserRoleService.list(userRoleWrapper);

        if (userRoles == null || userRoles.isEmpty()) {
            return java.util.Collections.emptySet();
        }

        // 2. 获取所有角色ID
        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());

        // 3. 查询角色权限关联
        LambdaQueryWrapper<SysRolePermission> rolePermissionWrapper = new LambdaQueryWrapper<>();
        rolePermissionWrapper.in(SysRolePermission::getRoleId, roleIds);
        List<SysRolePermission> rolePermissions = sysRolePermissionService.list(rolePermissionWrapper);

        if (rolePermissions == null || rolePermissions.isEmpty()) {
            return java.util.Collections.emptySet();
        }

        // 4. 获取所有权限ID并去重
        return rolePermissions.stream()
                .map(SysRolePermission::getPermissionId)
                .collect(Collectors.toSet());
    }
}
