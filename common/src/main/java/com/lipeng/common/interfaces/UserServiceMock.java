package com.lipeng.common.interfaces;

import com.lipeng.common.vo.ResultVo;
import com.lipeng.common.entity.User;
import com.lipeng.common.vo.UserVo;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: lipeng
 * @Date: 2020/11/24 11:18
 * @Description mock要和接口在同一个包下面
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

    @Override
    public ResultVo getUserV3(UserVo userVo) {
        log.error("getUserV3降级");
        return ResultVo.fail("getUserV3降级");
    }

    @Override
    public ResultVo getUserV4(UserVo userVo) {
        log.error("getUserV4降级");
        return ResultVo.fail("getUserV4降级");
    }

}