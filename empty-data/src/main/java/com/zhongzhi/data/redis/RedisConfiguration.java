package com.zhongzhi.data.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * redis配置
 * @author liuh
 * @date 2021年5月20日
 */
@Configuration
public class RedisConfiguration {

	@Value("${spring.redis.host}")
	private String host;

	@Value("${spring.redis.port}")
	private int port;
	
	@Value("${spring.redis.database}")
	private Integer database;

	@Value("${spring.redis.pool.max-active}")
	private int maxTotal;

	@Value("${spring.redis.pool.max-idle}")
	private int maxIdle;

	@Value("${spring.redis.pool.min-idle}")
	private int minIdle;

	@Value("${spring.redis.timeout}")
	private int connTimeout;

	@Value("${spring.redis.pool.max-wait}")
	private int maxWaitMillis;

	@Value("${spring.redis.password}")
	private String password;


	@Bean(name = "jedis.pool")
	@Autowired
	public JedisPool jedisPool(@Qualifier("jedis.pool.config") JedisPoolConfig config) {
		if (!password.equals("")) {
			return new JedisPool(config, host, port,maxWaitMillis,password,database);
		}
		return new JedisPool(config, host, port,database);
	}

	@Bean(name = "jedis.pool.config")
	public JedisPoolConfig jedisPoolConfig() {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(this.maxTotal);
		config.setMaxIdle(maxIdle);
		config.setMinIdle(minIdle);
		config.setMaxWaitMillis(maxWaitMillis);
		config.setTestOnBorrow(false);
		config.setTestOnReturn(false);
		return config;
	}

	@Bean
	public JedisConnectionFactory connectionFactory() {
		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
		jedisConnectionFactory.setUsePool(true);
		jedisConnectionFactory.setPoolConfig(this.jedisPoolConfig());
		jedisConnectionFactory.setHostName(host);
		jedisConnectionFactory.setPort(port);
		if (!password.equals("")) {
			jedisConnectionFactory.setPassword(password);
		}
		return jedisConnectionFactory;
	}

	@Bean
	public RedisTemplate<?, ?> redisTemplate() {
		RedisTemplate<?, ?> redisTemplate = new RedisTemplate<Object, Object>();
		redisTemplate.setConnectionFactory(connectionFactory());

		// 自定义的string序列化器和fastjson序列化器
		StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

		// jackson 序列化器
		GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer();

		// fastjson 序列化器
//        GenericFastJsonRedisSerializer jsonRedisSerializer = new GenericFastJsonRedisSerializer();

		// kv 序列化
		redisTemplate.setKeySerializer(stringRedisSerializer);
		redisTemplate.setValueSerializer(jsonRedisSerializer);

		// hash 序列化
		redisTemplate.setHashKeySerializer(stringRedisSerializer);
		redisTemplate.setHashValueSerializer(jsonRedisSerializer);

		redisTemplate.afterPropertiesSet();

		return redisTemplate;
	}
}

