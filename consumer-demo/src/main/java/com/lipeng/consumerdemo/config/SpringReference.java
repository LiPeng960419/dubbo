package com.lipeng.consumerdemo.config;

import java.lang.annotation.*;

/**
 * @Author: lipeng
 * @Date: 2021/03/24 15:51
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SpringReference {

    String version() default "";

    String mock() default "";

    String stub() default "";

}