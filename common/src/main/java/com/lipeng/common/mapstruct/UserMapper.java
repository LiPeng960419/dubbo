package com.lipeng.common.mapstruct;

import com.lipeng.common.dto.UserDto;
import com.lipeng.common.entity.User;
import com.lipeng.common.vo.UserVo;
import org.mapstruct.Mapper;

/**
 * @Author: lipeng
 * @Date: 2020/11/26 14:46
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto convert(UserVo userVo);

    User convert(UserDto userDto);

}