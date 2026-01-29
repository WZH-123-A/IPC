package com.ccs.ipc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.ccs.ipc.mapper") // 扫描 mapper 接口
@EnableAsync // 开启异步支持，用于异步保存日志
@EnableScheduling // 开启定时任务，用于每日统计采集
public class IpcApplication {

    public static void main(String[] args) {
        SpringApplication.run(IpcApplication.class, args);
    }

}
