package com.lipeng.consumerdemo.config;

import com.alibaba.dubbo.config.ConsumerConfig;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.util.Set;

@Slf4j
@Component
public class SpringReferenceRunner implements CommandLineRunner, ApplicationContextAware {

    @Autowired
    private DubboReferenceFactory factory;

    private static ApplicationContext applicationContext;

    private static boolean inited = false;
    private static boolean shouldBreak = false;

    /**
     * 如果消费组这里设置reference时 提供者没启动 获取到的reference为null
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) {
        String packageName = "com.lipeng.consumerdemo";
        Reflections f = new Reflections(packageName);
        Set<Class<?>> set = f.getTypesAnnotatedWith(RestController.class);
        new Thread(() -> {
            while (!inited) {
                try {
                    for (Class<?> c : set) {
                        if (shouldBreak) {
                            break;
                        }
                        if (c.isAnnotationPresent(RestController.class)) {
                            Field[] declaredFields = c.getDeclaredFields();
                            for (Field field : declaredFields) {
                                if (field.isAnnotationPresent(SpringReference.class)) {
                                    field.setAccessible(true);
                                    SpringReference annotation = field.getAnnotation(SpringReference.class);
                                    String mock = annotation.mock();
                                    String stub = annotation.stub();
                                    String version = annotation.version();
                                    ConsumerConfig consumerConfig = new ConsumerConfig();
                                    consumerConfig.setMock(mock);
                                    consumerConfig.setStub(stub);
                                    Object bean = applicationContext.getBean(c);
                                    Class<?> type = field.getType();
                                    Object dubboBean = factory.getDubboBean(type, version, consumerConfig);
                                    if (dubboBean == null) {
                                        shouldBreak = true;
                                        break;
                                    }
                                    field.set(bean, dubboBean);
                                    inited = true;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("SpringReference init error", e);
                }
            }
        }).start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringReferenceRunner.applicationContext = applicationContext;
    }

}