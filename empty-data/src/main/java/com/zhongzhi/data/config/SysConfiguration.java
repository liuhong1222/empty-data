package com.zhongzhi.data.config;


import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import com.zhongzhi.data.util.Snowflake;

@Configurable
public class SysConfiguration {

    @Value("${sys.work-id:0}")
    private Long workId;

    @Value("${sys.data-center-id:0}")
    private Long dataCenterId;

    @Bean(name="snowflake")
    public Snowflake snowflake(){
        return new Snowflake(workId,dataCenterId);
    }
}
