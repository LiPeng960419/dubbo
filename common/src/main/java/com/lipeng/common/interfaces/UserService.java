package com.lipeng.common.interfaces;


import com.lipeng.common.entity.User;
import com.lipeng.common.vo.ResultVo;
import com.lipeng.common.vo.UserVo;

/**
 * @Author: lipeng 910138
 * @Date: 2019/9/23 15:51
 */
public interface UserService {

    String getUser(Long userId);

    ResultVo<User> getUserV1(String name);

    ResultVo getUserV2(String name);

    ResultVo getUserV3(UserVo userVo);

}