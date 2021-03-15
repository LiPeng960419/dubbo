package com.lipeng.consumerdemo.config;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author: lipeng
 * @Date: 2021/03/15 10:48
 */
@Slf4j
@Component
public class DubboReferenceConfig implements InitializingBean {

    @Value("${dubbo.application.name}")
    private String applicationName1;
    private static String applicationName;

    @Value("${dubbo.registries.prod.address}")
    private String address1;
    private static String address;

    @Value("${dubbo.registries.gray.address}")
    private String grayAddress1;
    private static String grayAddress;

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
            // 当前应用配置
            ApplicationConfig application = new ApplicationConfig();
            application.setName(applicationName);
            // 连接注册中心配置
            RegistryConfig registry = new RegistryConfig();
            registry.setProtocol("zookeeper");
            if (isGray) {
                registry.setAddress(grayAddress);
            } else {
                registry.setAddress(address);
            }
            // 注意：ReferenceConfig为重对象，内部封装了与注册中心的连接，以及与服务提供方的连接
            // 引用远程服务
            ReferenceConfig reference = new ReferenceConfig();
            reference.setApplication(application);
            // 多个注册中心可以用setRegistries()
            reference.setCheck(false);
            reference.setRegistry(registry);
            reference.setInterface(dubboClasss);
            reference.setVersion(StringUtils.isEmpty(dubboVersion) ? "1.0.0" : dubboVersion);
            Object obj = reference.get();
            return (T) obj;
        } catch (Exception e) {
            log.error("getDubboBean error", e);
            return null;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        applicationName = applicationName1;
        address = address1;
        grayAddress = grayAddress1;
    }

}