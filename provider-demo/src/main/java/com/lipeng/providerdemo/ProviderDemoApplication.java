package com.lipeng.providerdemo;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.ComponentScan;

@EnableDubbo
@SpringBootApplication
@EnableHystrix
@ComponentScan({"com.lipeng.common", "com.lipeng.providerdemo"})
public class ProviderDemoApplication {

    public static void main(String[] args) {
        System.setProperty("app.name", "ProviderDemoApplication");
        SpringApplication app = new SpringApplication(ProviderDemoApplication.class);
        app.setRegisterShutdownHook(false);
        app.run(args);
    }

}