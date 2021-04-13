package com.lipeng.providerdemo.config;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.utils.ConfigUtils;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

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
        log.info("[SpringBootShutdownHook] Register ShutdownHook....");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                System.setProperty(Constants.SHUTDOWN_WAIT_KEY, "8000");
                int timeOut = ConfigUtils.getServerShutdownTimeout();
                log.info("[SpringBootShutdownHook] Application need sleep {} seconds to wait Dubbo shutdown", (double) timeOut / 1000.0D);
                Thread.sleep(timeOut);
                log.info("[SpringBootShutdownHook] Ready to close configurableApplicationContext");
                configurableApplicationContext.close();
                log.info("[SpringBootShutdownHook] ApplicationContext closed, Application shutdown");
            } catch (InterruptedException e) {
                log.info("[SpringBootShutdownHook] Dubbo shutdown hook close error");
            }
        }));
    }

}