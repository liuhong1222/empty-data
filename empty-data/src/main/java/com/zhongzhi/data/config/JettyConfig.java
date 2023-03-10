package com.zhongzhi.data.config;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * jetty自动配置
 * @author liuh
 * @date 2021年11月8日
 */
@Configuration
public class JettyConfig {

	@Bean
    public ConfigurableServletWebServerFactory jettyEmbeddedServletContainerFactory(
            JettyServerCustomizer jettyServerCustomizer) {
		JettyServletWebServerFactory factory = new JettyServletWebServerFactory();
        factory.addServerCustomizers(jettyServerCustomizer);
        return factory;
    }


    @Bean
    public JettyServerCustomizer jettyServerCustomizer() {
        return server -> {
            threadPool(server);
        };
    }

    private void threadPool(Server server){
        // Tweak the connection config used by Jetty to handle incoming HTTP
        // connections
        final QueuedThreadPool threadPool = server.getBean(QueuedThreadPool.class);
        //默认最大线程连接数600
        threadPool.setMaxThreads(800);
        //默认最小线程连接数200
        threadPool.setMinThreads(200);
        //默认线程最大空闲时间60000ms
        threadPool.setIdleTimeout(60000);
    }
}
