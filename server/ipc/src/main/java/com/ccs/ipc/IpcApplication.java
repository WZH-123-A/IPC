package com.ccs.ipc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ccs.ipc.mapper") //扫描mapper接口
public class IpcApplication {

    public static void main(String[] args) {
        SpringApplication.run(IpcApplication.class, args);
    }

}
