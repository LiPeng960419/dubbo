package com.lipeng.consumerdemo.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.lipeng.common.interfaces.UserService;
import com.lipeng.common.vo.ResultVo;
import com.lipeng.common.entity.User;
import com.lipeng.common.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: lipeng 910138
 * @Date: 2019/9/23 16:41
 */
@RestController
@Slf4j
public class OrderController {

    /*
    check 检查提供方服务是否可用
    timeout 调用提供方延时
    retries 第一次失败后 之后重试次数
    version 使用提供方的版本 可以指定 *表示任意
    url dubbo直连服务提供方  url为提供方路径 如127.0.0.1:7001
    loadbalance 负载均衡 ramdon roundrobin leastactive 随机，轮询，最少活跃调用
     */
    @Reference(check = true, retries = 2, timeout = 5000, mock = "true",
            version = "*", stub = "com.lipeng.consumerdemo.service.UserServiceStub")
    private UserService userService;

    @GetMapping("/order/{userId}")
    public String order(@PathVariable Long userId) {
        log.info("OrderController order userId:" + userId);
        return userService.getUser(userId);
    }

    /*
    v1和v2返回都是对象User
     */

    @GetMapping("/getUserV1")
    public ResultVo getUserV1(String name) {
        ResultVo<User> userV1 = userService.getUserV1(name);
        return userV1;
    }

    @GetMapping("/getUserV2")
    public ResultVo getUserV2(String name) {
        ResultVo userV2 = userService.getUserV2(name);
        return userV2;
    }

    @GetMapping("/getUserV3")
    public ResultVo getUserV3(UserVo vo) {
        ResultVo userV2 = userService.getUserV3(vo);
        return userV2;
    }

}