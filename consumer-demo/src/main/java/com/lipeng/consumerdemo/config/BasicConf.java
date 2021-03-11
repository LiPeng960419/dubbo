package com.lipeng.consumerdemo.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author: lipeng
 * @Date: 2021/03/11 10:28
 */
@Component
@Data
public class BasicConf {

    @Value("${gray.grayPushUsers}")
    private String grayPushUsers;

    @Value("${gray.grayPushIps}")
    private String grayPushIps;

}