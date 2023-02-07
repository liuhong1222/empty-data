package com.zhongzhi.data.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 自定义注解 限流
 *
 * @author rivers
 * @since 2021-05-08
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Limiter {
    /*
     限制数量
     */
    double limitNum() default 10;      //默认每秒产生10个令牌

    /*
     key
     */
    String key() default "";

    /*
     获取令牌最大等待时间
     */
    long timeout() default 500;

    /*
     单位(例:分钟/秒/毫秒) 默认:毫秒
     */
    TimeUnit timeunit() default TimeUnit.MILLISECONDS;

    /*
     限流类型
     */
    Limiter.LimitType limitType() default LimitType.DEFAULT;

    enum LimitType {
        /*
         默认策略：根据请求方法名
         */
        DEFAULT,
        /*
         自定义key
         */
        CUSTOMER,
        /*
         根据请求者IP
         */
        IP
    }
}
