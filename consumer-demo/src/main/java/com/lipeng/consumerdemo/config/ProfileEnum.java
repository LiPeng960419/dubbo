package com.lipeng.consumerdemo.config;

/**
 * @Author: lipeng
 * @Date: 2021/03/18 13:36
 */
public enum ProfileEnum {

    PROD("prod", "正式环境"),
    GRAY("gray", "灰度环境");

    public static final String PROFILE = "profile";

    private String code;

    private String desc;

    ProfileEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}