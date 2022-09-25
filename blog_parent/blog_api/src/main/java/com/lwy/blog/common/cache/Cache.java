package com.lwy.blog.common.cache;


import java.lang.annotation.*;

/**
 * @author 李文阳
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cache {

    String name() default "";

    long expire() default 1 * 60 * 1000;
}
