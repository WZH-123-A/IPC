package com.ccs.ipc;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.nio.file.Paths;

public class CodeGenerator {

    public static void main(String[] args) {
        String username = "ipc_user";
        String url = "jdbc:mysql://8.162.11.216:3306/ipc_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String password = "WZHSGSR200381";
        FastAutoGenerator.create(url, username, password)
                .globalConfig(builder -> builder
                        .author("WZH")
                        .outputDir(Paths.get(System.getProperty("user.dir")) + "/src/main/java")
                        .commentDate("yyyy-MM-dd")
                )
                .packageConfig(builder -> builder
                        .parent("com.ccs.ipc")
                        .entity("entity")
                        .mapper("mapper")
                        .service("service")
                        .serviceImpl("service.impl")
                        .xml("mapper.xml")
                )
                .strategyConfig(builder -> builder
                        .entityBuilder()
                        .enableLombok()
                )
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
