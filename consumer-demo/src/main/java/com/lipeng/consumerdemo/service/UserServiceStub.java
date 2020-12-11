package com.lipeng.consumerdemo.service;

import com.lipeng.common.interfaces.UserService;
import com.lipeng.common.vo.ResultVo;
import com.lipeng.common.entity.User;
import com.lipeng.common.vo.UserVo;
import org.springframework.util.StringUtils;

/**
 * @Author: lipeng 910138
 * @Date: 2020/10/15 15:30
 */
public class UserServiceStub implements UserService {

    /*
     可以在userService 先调用本地存根代码，作用主要是校验参数等
     构造函数里面传入远程代理对象
     */
    private final UserService userService;

    public UserServiceStub(UserService userService) {
        super();
        this.userService = userService;
    }

    @Override
    public String getUser(Long userId) {
        return userService.getUser(userId);
    }

    @Override
    public ResultVo<User> getUserV1(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        return userService.getUserV1(name);
    }

    @Override
    public ResultVo getUserV2(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        return userService.getUserV2(name);
    }

    @Override
    public ResultVo getUserV3(UserVo userVo) {
        if (userVo == null) {
            return null;
        }
        if (StringUtils.isEmpty(userVo.getName())) {
            return null;
        }
        return userService.getUserV3(userVo);
    }

    @Override
    public ResultVo getUserV4(UserVo userVo) {
        if (userVo == null) {
            return null;
        }
        if (StringUtils.isEmpty(userVo.getName())) {
            return null;
        }
        return userService.getUserV3(userVo);
    }

}