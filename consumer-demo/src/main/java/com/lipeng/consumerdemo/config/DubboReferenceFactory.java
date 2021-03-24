package com.lipeng.consumerdemo.config;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.utils.ReferenceConfigCache;
import com.lipeng.common.utils.IpTraceUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;

/**
 * @Author: lipeng
 * @Date: 2021/03/15 10:48
 */
@Slf4j
@Component
public class DubboReferenceFactory {

    private static final String DEFAULT_VERSION = "1.0.0";
    private static final String KEY_REFERENCE_PROD = DubboReferenceFactory.class.getName() + "_key_reference_prod";
    private static final String KEY_REFERENCE_GRAY = DubboReferenceFactory.class.getName() + "_key_reference_gray";

    @Autowired
    private BasicConf basicConf;

    @Autowired
    @Qualifier("grayRegistryConfig")
    private RegistryConfig grayRegistryConfig;

    @Autowired
    @Qualifier("prodRegistryConfig")
    private RegistryConfig prodRegistryConfig;

    @Autowired
    private ConsumerConfig consumerConfig;

    @Autowired
    private ApplicationConfig applicationConfig;

    public <T> T getDubboBean(Class<T> dubboClasss, String dubboVersion) {
        return getDubboBean(dubboClasss, dubboVersion, null);
    }

    /**
     * 动态获取线上环境或者灰度环境的bean
     *
     * @param dubboClasss
     * @param dubboVersion
     * @param consumerConfig
     * @param <T>
     * @return
     */
    public <T> T getDubboBean(Class<T> dubboClasss, String dubboVersion, ConsumerConfig customConsumerConfig) {
        try {
            //HashSet<String> users = new HashSet<>(Arrays.asList(basicConf.getGrayPushUsers().split(",")));
            HashSet<String> ips = new HashSet<>(Arrays.asList(basicConf.getGrayPushIps().split(",")));

            // 连接注册中心配置
            boolean gray = ips.contains(IpTraceUtils.getIp());
            if (gray) {
                log.info("当前用户IP:{}访问灰度服务", IpTraceUtils.getIp());
            }
            RegistryConfig registryConfig = gray ? grayRegistryConfig : prodRegistryConfig;
            // 注意：ReferenceConfig为重对象，内部封装了与注册中心的连接，以及与服务提供方的连接
            // 引用远程服务
            ReferenceConfig<T> reference = new ReferenceConfig<T>();
            // 当前应用配置
            reference.setApplication(applicationConfig);
            // 消费端配置
            if (customConsumerConfig != null) {
                ConsumerConfig c = new ConsumerConfig();
                BeanUtils.copyProperties(consumerConfig, c);
                BeanUtils.copyProperties(customConsumerConfig, c);
                reference.setConsumer(c);
            } else {
                reference.setConsumer(consumerConfig);
            }
            // 多个注册中心可以用setRegistries()
            reference.setRegistry(registryConfig);
            reference.setInterface(dubboClasss);
            reference.setVersion(StringUtils.isEmpty(dubboVersion) ? DEFAULT_VERSION : dubboVersion);
            // 由于这里通过key来区分线上和灰度 所以不用重写KeyGenerator
            // 如果同一服务 想要进行差异化调用 则可以在里面重写
            return gray ? ReferenceConfigCache.getCache(KEY_REFERENCE_GRAY).get(reference)
                    : ReferenceConfigCache.getCache(KEY_REFERENCE_PROD).get(reference);
//            return gray ? ReferenceConfigCache.getCache(KEY_REFERENCE_GRAY, new CustomKeyGenerator(gray)).get(reference)
//                    : ReferenceConfigCache.getCache(KEY_REFERENCE_PROD, new CustomKeyGenerator(gray)).get(reference);
        } catch (Exception e) {
            log.error("getDubboBean error", e);
            return null;
        }
    }

    @Data
    @AllArgsConstructor
    private static class CustomKeyGenerator implements ReferenceConfigCache.KeyGenerator {

        private boolean isGray;

        @Override
        public String generateKey(ReferenceConfig<?> referenceConfig) {
            String iName = referenceConfig.getInterface();
            if (com.alibaba.dubbo.common.utils.StringUtils.isBlank(iName)) {
                Class<?> clazz = referenceConfig.getInterfaceClass();
                iName = clazz.getName();
            }

            if (com.alibaba.dubbo.common.utils.StringUtils.isBlank(iName)) {
                throw new IllegalArgumentException("No interface info in ReferenceConfig" + referenceConfig);
            } else {
                StringBuilder ret = new StringBuilder();
                if (!com.alibaba.dubbo.common.utils.StringUtils.isBlank(referenceConfig.getGroup())) {
                    ret.append(referenceConfig.getGroup()).append("/");
                }

                ret.append(iName);
                if (!com.alibaba.dubbo.common.utils.StringUtils.isBlank(referenceConfig.getVersion())) {
                    ret.append(":").append(referenceConfig.getVersion());
                }

                if (isGray) {
                    ret.append(":").append(ProfileEnum.GRAY.getCode());
                } else {
                    ret.append(":").append(ProfileEnum.PROD.getCode());
                }

                return ret.toString();
            }
        }
    }

}