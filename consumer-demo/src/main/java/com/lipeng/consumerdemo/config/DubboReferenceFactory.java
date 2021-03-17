package com.lipeng.consumerdemo.config;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.lipeng.common.utils.IpTraceUtils;
import java.util.Arrays;
import java.util.HashSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @Author: lipeng
 * @Date: 2021/03/15 10:48
 */
@Slf4j
@Component
public class DubboReferenceFactory {

	private static final String DEFAULT_VERSION = "1.0.0";

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
		return getDubboBean(dubboVersion, dubboClasss);
	}

	public <T> T getGrayDubboBean(Class<T> dubboClasss) {
		return getDubboBean(null, dubboClasss);
	}

	public <T> T getGrayDubboBean(Class<T> dubboClasss, String dubboVersion) {
		return getDubboBean(dubboVersion, dubboClasss);
	}

	/**
	 * 动态获取线上环境或者灰度环境的bean，根据isGray
	 *
	 * @param dubboVersion
	 * @param dubboClasss
	 * @param <T>
	 * @return
	 */
	public <T> T getDubboBean(String dubboVersion, Class<T> dubboClasss) {
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

}