package com.lipeng.common.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: lipeng
 * @Date: 2020/11/26 9:25
 */
@Data
public class UserDto implements Serializable {

    private static final long serialVersionUID = -2504076438882618854L;
    private String name;

}