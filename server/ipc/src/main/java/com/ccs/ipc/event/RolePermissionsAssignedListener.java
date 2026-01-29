package com.ccs.ipc.event;

import com.ccs.ipc.entity.SysUserRole;
import com.ccs.ipc.service.ISysUserRoleService;
import com.ccs.ipc.websocket.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色权限分配完成后，在事务提交后通知该角色下所有用户刷新权限（避免在事务内发 WebSocket 导致锁等待超时）
 */
@Slf4j
@Component
public class RolePermissionsAssignedListener {

    @Autowired
    private ISysUserRoleService sysUserRoleService;

    @Autowired
    private WebSocketService webSocketService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRolePermissionsAssigned(RolePermissionsAssignedEvent event) {
        Long roleId = event.getRoleId();
        if (roleId == null) return;

        try {
            var queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUserRole>();
            queryWrapper.eq(SysUserRole::getRoleId, roleId);
            List<SysUserRole> userRoles = sysUserRoleService.list(queryWrapper);
            if (userRoles.isEmpty()) return;

            List<Long> userIds = userRoles.stream()
                    .map(SysUserRole::getUserId)
                    .distinct()
                    .collect(Collectors.toList());
            webSocketService.sendPermissionRefreshToUsers(userIds);
            log.debug("事务提交后已通知 {} 个用户刷新权限，roleId={}", userIds.size(), roleId);
        } catch (Exception e) {
            log.warn("通知用户刷新权限失败，roleId={}", roleId, e);
        }
    }
}
