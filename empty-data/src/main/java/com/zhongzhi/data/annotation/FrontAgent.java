package com.zhongzhi.data.annotation;

import java.lang.annotation.*;


/**
 * 前端网站代理商
 *
 * @author rivers
 * @since 2020/2/17 下午2:40
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FrontAgent {
    String value() default "";

}
