package com.lipeng.consumerdemo.config;

import com.alibaba.dubbo.common.utils.ConfigUtils;
import com.alibaba.dubbo.config.RegistryConfig;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @Author: lipeng
 * @Date: 2020/12/11 17:23
 */
@Component
@Slf4j
public class DubboConfig {

    @Bean("prodRegistryConfig")
    @ConfigurationProperties(prefix = "dubbo.registries.prod")
    public RegistryConfig prodRegistryConfig() {
        return new RegistryConfig();
    }

    @Bean("grayRegistryConfig")
    @ConfigurationProperties(prefix = "dubbo.registries.gray")
    public RegistryConfig grayRegistryConfig() {
        return new RegistryConfig();
    }

    @Autowired
    private ConfigurableApplicationContext configurableApplicationContext;

    @PostConstruct
    public void registerShutdownHook() {
        log.info("[SpringBootShutdownHook] Register ShutdownHook....");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                System.setProperty("dubbo.service.shutdown.wait", "2000");
                int timeOut = ConfigUtils.getServerShutdownTimeout();
                log.info("[SpringBootShutdownHook] Application need sleep {} seconds to wait Dubbo shutdown", (double) timeOut / 1000.0D);
                Thread.sleep(timeOut);
                configurableApplicationContext.close();
                log.info("[SpringBootShutdownHook] ApplicationContext closed, Application shutdown");
            } catch (InterruptedException e) {
                log.info("[SpringBootShutdownHook] Dubbo shutdown hook close error");
            }
        }));
    }

}