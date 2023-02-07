package com.zhongzhi.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.zhongzhi.data.config.SysConfiguration;

@SpringBootApplication
@Import(SysConfiguration.class)
public class EmptyDataApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmptyDataApplication.class, args);
	}
}
