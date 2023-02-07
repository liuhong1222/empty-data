package com.zhongzhi.data.config;


import com.zhongzhi.data.interceptor.BeforeLoginInterceptor;
import com.zhongzhi.data.interceptor.CommonInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置
 * @author liuh
 * @date 2021年3月8日
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer{

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getBeforeLoginInterceptor()).addPathPatterns("/front/login").addPathPatterns("/front/userlogin");
        registry.addInterceptor(getCommonInterceptor()).excludePathPatterns("/front/login").excludePathPatterns("/front/userlogin")
				.excludePathPatterns("/pm/sys/user/forgetPwd/**");
    }
	
	@Bean
	public CommonInterceptor getCommonInterceptor() {
		return new CommonInterceptor();
	}
	
	@Bean
	public BeforeLoginInterceptor getBeforeLoginInterceptor() {
		return new BeforeLoginInterceptor();
	}

}
