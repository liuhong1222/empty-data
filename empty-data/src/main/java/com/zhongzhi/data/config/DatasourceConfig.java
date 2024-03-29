package com.zhongzhi.data.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class DatasourceConfig {
	
	private static Logger logger = LoggerFactory.getLogger(DatasourceConfig.class);
    
    @Value("${spring.datasource.url}")  
    private String dbUrl;  
      
    @Value("${spring.datasource.type}")  
    private String dbType;  
      
    @Value("${spring.datasource.username}")  
    private String username;  
      
    @Value("${spring.datasource.password}")  
    private String password;  
      
    @Value("${spring.datasource.driver-class-name}")  
    private String driverClassName;  
      
    @Value("${spring.datasource.initialSize}")  
    private int initialSize;  
      
    @Value("${spring.datasource.minIdle}")  
    private int minIdle;  
      
    @Value("${spring.datasource.maxActive}")  
    private int maxActive;  
      
    @Value("${spring.datasource.maxWait}")  
    private int maxWait;  
      
    @Value("${spring.datasource.timeBetweenEvictionRunsMillis}")  
    private int timeBetweenEvictionRunsMillis;  
      
    @Value("${spring.datasource.minEvictableIdleTimeMillis}")  
    private int minEvictableIdleTimeMillis;  
      
    @Value("${spring.datasource.validationQuery}")  
    private String validationQuery;  
      
    @Value("${spring.datasource.testWhileIdle}")  
    private boolean testWhileIdle;  
      
    @Value("${spring.datasource.testOnBorrow}")  
    private boolean testOnBorrow;  
      
    @Value("${spring.datasource.testOnReturn}")  
    private boolean testOnReturn;  
      
    @Value("${spring.datasource.poolPreparedStatements}")  
    private boolean poolPreparedStatements;  
      
    @Value("${spring.datasource.filters}")  
    private String filters;  
      
    @Value("${spring.datasource.connectionProperties}")  
    private String connectionProperties;  
      
    @Value("${spring.datasource.useGlobalDataSourceStat}")  
    private boolean useGlobalDataSourceStat;  
      
    @Value("${spring.datasource.druidLoginName}")  
    private String druidLoginName;  
      
    @Value("${spring.datasource.druidPassword}")  
    private String druidPassword;
    
    @Bean(name="dataSource",destroyMethod = "close", initMethod="init")  
    @Primary //不要漏了�?  
    public DataSource dataSource(){    
        DruidDataSource datasource = new DruidDataSource();    
        try {    
            datasource.setUrl(this.dbUrl);    
            //datasource.setDbType(dbType);
            datasource.setUsername(username);    
            datasource.setPassword(password);    
            datasource.setDriverClassName(driverClassName);    
            datasource.setInitialSize(initialSize);    
            datasource.setMinIdle(minIdle);    
            datasource.setMaxActive(maxActive);    
            datasource.setMaxWait(maxWait);    
            datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);    
            datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);    
            datasource.setValidationQuery(validationQuery);    
            datasource.setTestWhileIdle(testWhileIdle);    
            datasource.setTestOnBorrow(testOnBorrow);    
            datasource.setTestOnReturn(testOnReturn);    
            datasource.setPoolPreparedStatements(poolPreparedStatements);    
            datasource.setFilters(filters);    
            datasource.setConnectionProperties(connectionProperties);  
            datasource.setUseGlobalDataSourceStat(useGlobalDataSourceStat);  
        } catch (SQLException e) {    
            logger.error("druid configuration initialization filter", e);    
        }    
        return datasource;    
    }
    
    
    /////////  下面是druid 监控访问的设�?  /////////////////  
    @Bean  
    public ServletRegistrationBean<Servlet> druidServlet() {  
        ServletRegistrationBean<Servlet> reg = new ServletRegistrationBean<Servlet>();  
        reg.setServlet(new StatViewServlet());  
        reg.addUrlMappings("/druid/*");  //url 匹配  
        reg.addInitParameter("allow", "192.168.16.110,127.0.0.1"); // IP白名�? (没有配置或�?�为空，则允许所有访�?)  
        reg.addInitParameter("deny", "192.168.16.111"); //IP黑名�? (存在共同时，deny优先于allow)  
        reg.addInitParameter("loginUsername", this.druidLoginName);//登录�?  
        reg.addInitParameter("loginPassword", this.druidPassword);//登录密码  
        reg.addInitParameter("resetEnable", "false"); // 禁用HTML页面上的“Reset All”功�?  
        return reg;  
    }  
  
    @Bean(name="druidWebStatFilter")  
    public FilterRegistrationBean<Filter> filterRegistrationBean() {  
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<Filter>();  
        filterRegistrationBean.setFilter(new WebStatFilter());  
        filterRegistrationBean.addUrlPatterns("/*");  
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*"); //忽略资源  
        filterRegistrationBean.addInitParameter("profileEnable", "true");  
        filterRegistrationBean.addInitParameter("principalCookieName", "USER_COOKIE");  
        filterRegistrationBean.addInitParameter("principalSessionName", "USER_SESSION");  
        return filterRegistrationBean;  
    }  
}
