package com.lipeng.providerdemo.config;

import com.alibaba.dubbo.common.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Author: lipeng
 * @Date: 2020/12/11 17:23
 */
@Component
@Slf4j
public class DubboConfig {

    @Autowired
    private ConfigurableApplicationContext configurableApplicationContext;

    @PostConstruct
    public void registerShutdownHook() {
        log.info("[DubboConfig] Register ShutdownHook....");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                System.setProperty("dubbo.service.shutdown.wait", "5000");
                int timeOut = ConfigUtils.getServerShutdownTimeout();
                log.info("[DubboConfig] Application need sleep {} seconds to wait Dubbo shutdown", (double) timeOut / 1000.0D);
                Thread.sleep(timeOut);
                configurableApplicationContext.close();
                log.info("[DubboConfig] ApplicationContext closed, Application shutdown");
            } catch (InterruptedException e) {
                log.info("[DubboConfig] Dubbo shutdown hook close error");
            }
        }));
    }

}