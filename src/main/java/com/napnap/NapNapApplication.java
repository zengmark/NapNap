package com.napnap;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@MapperScan("com.napnap.mapper")
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableJpaRepositories(basePackages = "com.napnap.config.handler")
public class NapNapApplication {
    public static void main(String[] args) {
        SpringApplication.run(NapNapApplication.class);
    }
}
