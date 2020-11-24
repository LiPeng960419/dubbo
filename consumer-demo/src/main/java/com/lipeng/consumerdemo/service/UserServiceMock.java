package com.lipeng.consumerdemo.service;

import com.lipeng.common.interfaces.UserService;
import com.lipeng.common.vo.ResultVo;
import com.lipeng.common.vo.User;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: lipeng
 * @Date: 2020/11/24 11:18
 */
@Slf4j
public class UserServiceMock implements UserService {

    @Override
    public String getUser(Long userId) {
        log.error("getUser降级");
        return "";
    }

    @Override
    public ResultVo<User> getUserV1(String name) {
        log.error("getUserV1降级");
        return ResultVo.fail("getUserV1降级");
    }

    @Override
    public ResultVo getUserV2(String name) {
        log.error("getUserV2降级");
        return ResultVo.fail("getUserV2降级");
    }

}