package com.lipeng.common.entity;

import java.io.Serializable;
import lombok.Data;

/**
 * @Author: lipeng 910138
 * @Date: 2020/10/15 14:11
 */
@Data
public class User implements Serializable {

    private static final long serialVersionUID = -3016785855988881524L;

    private String name;

    private String password;

}