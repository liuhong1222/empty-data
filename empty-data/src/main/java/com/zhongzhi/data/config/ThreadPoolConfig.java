package com.zhongzhi.data.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.zhongzhi.data.constants.ThreadPoolConst.CORE_POOL_SIZE;
import static com.zhongzhi.data.constants.ThreadPoolConst.MAXIMUM_POOL_SIZE;


/**
 * 线程池配置类
 *
 * @author xiaoybb
 * @date 2021-03-18
 */
@Configuration
public class ThreadPoolConfig {
    // 创建自定义线程名称的ThreadFactory
    // ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("pm-thread-pool-%d").build();
    ThreadFactory namedThreadFactory = new NamedThreadFactory("em");

    /**
     * 手动创建线程池
     * @date 2021/3/18
     * @param
     * @return java.util.concurrent.ThreadPoolExecutor
     */
    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        return new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024),
                namedThreadFactory,
                new ThreadPoolExecutor.AbortPolicy());
    }

}
