package com.lipeng.consumerdemo.config;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.utils.ReferenceConfigCache;
import com.alibaba.dubbo.rpc.service.GenericService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @Author: lipeng
 * @Date: 2021/03/15 10:48
 */
@Slf4j
@Component
public class DubboReferenceUtils implements InitializingBean {

    private static final String DEFAULT_VERSION = "1.0.0";
    private static final String KEY_REFERENCE = "key_reference";
    private static final String KEY_INVOKE_REFERENCE = "key_invoke_reference";

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
     * 指定调用某个reference  如果想要动态获取bean 用另一个工具类
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
            ReferenceConfig<T> reference = new ReferenceConfig<T>();
            // 当前应用配置
            reference.setApplication(applicationConfig);
            // 消费端配置
            reference.setConsumer(consumerConfig);
            // 多个注册中心可以用setRegistries()
            reference.setRegistry(registryConfig);
            reference.setInterface(dubboClasss);
            reference.setVersion(StringUtils.isEmpty(dubboVersion) ? DEFAULT_VERSION : dubboVersion);
            ReferenceConfigCache cache = ReferenceConfigCache.getCache(KEY_REFERENCE);
            return cache.get(reference);
        } catch (Exception e) {
            log.error("getDubboBean error", e);
            return null;
        }
    }

    public static Object genericInvoke(Class interfaceClass, String methodName, List<Map<String, Object>> parameters) {
        return genericInvoke(interfaceClass, null, methodName, parameters);
    }

    public static Object genericInvoke(Class interfaceClass, String dubboVersion, String methodName, List<Map<String, Object>> parameters) {
        // 用com.alibaba.dubbo.rpc.service.GenericService可以替代所有接口引用
        ReferenceConfig<GenericService> reference = new ReferenceConfig<GenericService>();
        reference.setApplication(applicationConfig);
        reference.setConsumer(consumerConfig);
        reference.setRegistry(prodRegistryConfig);
        reference.setInterface(interfaceClass); // 接口名
        reference.setVersion(StringUtils.isEmpty(dubboVersion) ? DEFAULT_VERSION : dubboVersion);
        reference.setGeneric(true); // 声明为泛化接口
        /*ReferenceConfig实例很重，封装了与注册中心的连接以及与提供者的连接，
        需要缓存，否则重复生成ReferenceConfig可能造成性能问题并且会有内存和连接泄漏。
        API方式编程时，容易忽略此问题。
        这里使用dubbo内置的简单缓存工具类进行缓存*/
        ReferenceConfigCache cache = ReferenceConfigCache.getCache(KEY_INVOKE_REFERENCE);
        GenericService genericService = cache.get(reference);

        int len = parameters.size();
        String[] invokeParamTyeps = new String[len];
        Object[] invokeParams = new Object[len];
        for (int i = 0; i < len; i++) {
            invokeParamTyeps[i] = parameters.get(i).get("ParamType") + "";
            invokeParams[i] = parameters.get(i).get("Object");
        }
        return genericService.$invoke(methodName, invokeParamTyeps, invokeParams);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        prodRegistryConfig = prodRegistryConfig1;
        grayRegistryConfig = grayRegistryConfig1;
        consumerConfig = consumerConfig1;
        applicationConfig = applicationConfig1;
    }

}