package com.lipeng.providerdemo.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.lipeng.common.interfaces.UserService;
import com.lipeng.common.vo.ResultVo;
import com.lipeng.common.vo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

/**
 * @Author: lipeng 910138
 * @Date: 2019/9/23 16:38
 */
@Slf4j
@Service(version = "1.0.0")
public class UserServiceImplV1 implements UserService {

    @Value("${server.port}")
    private int port;

    @Override
    public String getUser(Long userId) {
        log.info("this is provider service,userId:" + userId + ",port:" + port);
        return "this is provider service,userId:" + userId + ",port:" + port;
    }

    @Override
    public ResultVo<User> getUserV1(String name) {
        // dubbo超时配置
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        User user = new User();
        user.setName(name);
        user.setPassword("UserServiceImplV1 passwordV1");
        return ResultVo.success(user);
    }

    @Override
    public ResultVo getUserV2(String name) {
        User user = new User();
        user.setName(name);
        user.setPassword("UserServiceImplV1 passwordV2");
        return ResultVo.success(user);
    }

}