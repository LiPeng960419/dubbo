package com.lipeng.consumerdemo.config;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @Author: lipeng
 * @Date: 2021/03/15 10:48
 */
@Slf4j
@Component
public class DubboReferenceUtils implements InitializingBean {

    private static final String DEFAULT_VERSION = "1.0.0";

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
    @Qualifier("grayRegistryConfig")
    private RegistryConfig grayRegistryConfig1;
    private static RegistryConfig grayRegistryConfig;

    @Autowired
    @Qualifier("prodRegistryConfig")
    private RegistryConfig prodRegistryConfig1;
    private static RegistryConfig prodRegistryConfig;

    @Autowired
    private ConsumerConfig consumerConfig1;
    private static ConsumerConfig consumerConfig;

    @Autowired
    private ApplicationConfig applicationConfig1;
    private static ApplicationConfig applicationConfig;

    public static <T> T getDubboBean(Class<T> dubboClasss, String dubboVersion) {
        return getDubboBean(dubboVersion, dubboClasss, false);
    }

    public static <T> T getGrayDubboBean(Class<T> dubboClasss) {
        return getDubboBean(null, dubboClasss, true);
    }

    public static <T> T getGrayDubboBean(Class<T> dubboClasss, String dubboVersion) {
        return getDubboBean(dubboVersion, dubboClasss, true);
    }

    /**
     * https://blog.csdn.net/DCBTB/article/details/102555612
     *
     * @param dubboVersion
     * @param dubboClasss
     * @param isGray
     * @param <T>
     * @return
     */
    public static <T> T getDubboBean(String dubboVersion, Class<T> dubboClasss, boolean isGray) {
        try {
            // 连接注册中心配置
            RegistryConfig registryConfig = isGray ? grayRegistryConfig : prodRegistryConfig;
            // 注意：ReferenceConfig为重对象，内部封装了与注册中心的连接，以及与服务提供方的连接
            // 引用远程服务
            ReferenceConfig reference = new ReferenceConfig();
            // 当前应用配置
            reference.setApplication(applicationConfig);
            // 消费端配置
            reference.setConsumer(consumerConfig);
            // 多个注册中心可以用setRegistries()
            reference.setRegistry(registryConfig);
            reference.setInterface(dubboClasss);
            reference.setVersion(StringUtils.isEmpty(dubboVersion) ? DEFAULT_VERSION : dubboVersion);
            Object obj = reference.get();
            return (T) obj;
        } catch (Exception e) {
            log.error("getDubboBean error", e);
            return null;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        prodRegistryConfig = prodRegistryConfig1;
        grayRegistryConfig = grayRegistryConfig1;
        consumerConfig = consumerConfig1;
        applicationConfig = applicationConfig1;
    }

}