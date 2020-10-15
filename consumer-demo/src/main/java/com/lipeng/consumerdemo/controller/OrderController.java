package com.lipeng.consumerdemo.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.lipeng.common.interfaces.UserService;
import com.lipeng.common.vo.ResultVo;
import com.lipeng.common.vo.User;
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

    @Reference
    private UserService userService;

    @GetMapping("/order/{userId}")
    public String order(@PathVariable Long userId) {
        log.info("OrderController order userId:" + userId);
        return userService.getUser(userId);
    }

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

}