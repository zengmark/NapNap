package com.napnap;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.napnap.mapper")
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class NapNapApplication {
    public static void main(String[] args) {
        SpringApplication.run(NapNapApplication.class);
    }
}
