package com.ccs.ipc.event;

import org.springframework.context.ApplicationEvent;

/**
 * 角色权限分配完成事件（在事务提交后由监听器发送 WebSocket 通知，避免事务内 I/O 导致锁等待超时）
 */
public class RolePermissionsAssignedEvent extends ApplicationEvent {

    private final Long roleId;

    public RolePermissionsAssignedEvent(Object source, Long roleId) {
        super(source);
        this.roleId = roleId;
    }

    public Long getRoleId() {
        return roleId;
    }
}
