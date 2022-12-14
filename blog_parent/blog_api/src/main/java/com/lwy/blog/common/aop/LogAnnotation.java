package com.lwy.blog.common.aop;

import java.lang.annotation.*;

/**
 * 日志注解
 * @author 李文阳
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogAnnotation {

    String module() default "";

    String operation() default "";
}