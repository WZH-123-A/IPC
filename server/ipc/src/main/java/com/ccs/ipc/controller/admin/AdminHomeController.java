package com.ccs.ipc.controller.admin;

import com.ccs.ipc.common.annotation.Log;
import com.ccs.ipc.common.annotation.RequirePermission;
import com.ccs.ipc.common.enums.log.AdminHomeModule;
import com.ccs.ipc.common.enums.log.AdminHomeOperation;
import com.ccs.ipc.common.response.Response;
import com.ccs.ipc.websocket.OnlineUserRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 管理员首页相关接口（数据概览等）
 *
 * @author WZH
 * @since 2026-01-29
 */
@RestController
@RequestMapping("/api/admin/home")
public class AdminHomeController {

    @Autowired
    private OnlineUserRegistry onlineUserRegistry;

    /**
     * 获取当前在线人数（基于 WebSocket 连接的去重用户数）
     */
    @GetMapping("/online-count")
    @RequirePermission("admin:api:user:list")
    @Log(operationType = AdminHomeOperation.C.GET_ONLINE_COUNT, operationModule = AdminHomeModule.C.ADMIN_HOME, operationDesc = "获取当前在线人数")
    public Response<Map<String, Integer>> getOnlineCount() {
        int count = onlineUserRegistry.getOnlineCount();
        return Response.success(Map.of("onlineCount", count));
    }
}
