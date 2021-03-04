package com.lipeng.providerdemo.config;

import com.alibaba.dubbo.config.spring.ServiceBean;
import com.alibaba.dubbo.config.spring.convert.converter.StringArrayToMapConverter;
import com.alibaba.dubbo.config.spring.convert.converter.StringArrayToStringConverter;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import java.beans.PropertyDescriptor;
import java.util.Map;

/**
 * @Author: lipeng
 * @Date: 2021/03/04 18:23
 */
@Configuration
public class ServiceParameterBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements PriorityOrdered {

    @Override
    public int getOrder() {
        return PriorityOrdered.LOWEST_PRECEDENCE;
    }

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {
        if (bean instanceof ServiceBean) {
            PropertyValue propertyValue = pvs.getPropertyValue("parameters");
            ConversionService conversionService = getConversionService();

            if (propertyValue != null && propertyValue.getValue() != null && conversionService.canConvert(propertyValue.getValue().getClass(), Map.class)) {
                Map parameters = conversionService.convert(propertyValue.getValue(), Map.class);
                propertyValue.setConvertedValue(parameters);
            }
        }
        return pvs;
    }

    private ConversionService getConversionService() {
        DefaultConversionService conversionService = new DefaultConversionService();
        conversionService.addConverter(new StringArrayToStringConverter());
        conversionService.addConverter(new StringArrayToMapConverter());
        return conversionService;
    }

}