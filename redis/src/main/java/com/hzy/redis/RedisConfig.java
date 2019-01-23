package com.hzy.redis;

import java.util.HashSet;
import java.util.Set;

import com.hzy.redis.annotation.Dev;
import com.hzy.redis.annotation.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;


import redis.clients.jedis.JedisPoolConfig;

@Configuration
@PropertySource("classpath:redis.properties")
public class RedisConfig {

	@Value("${redis.sentinel.cluster.name}")
	private String clsuterName;

	@Value("${redis.sentinel.nodes}")
	private String nodes;

	@Value("${redis.password}")
	private String password;

	@Value("${redis.maxtotal}")
	private Integer maxTotal;

	@Value("${redis.timeout}")
	private Integer timeout;

	@Value("${redis.maxIdle}")
	private Integer maxIdle;

	@Value("${redis.minIdle}")
	private Integer minIdle;

	@Value("${redis.maxWaitMillis}")
	private Long maxWaitMillis;

	@Value("${redis.testOnReturn}")
	private Boolean testOnReturn;

	@Value("${redis.testOnBorrow}")
	private Boolean testOnBorrow;

	@Value("${redis.blockWhenExhausted}")
	private Boolean blockWhenExhausted;

	/**
	 * redis连接池配置
	 * 
	 * @return
	 */
	@Bean
	public JedisPoolConfig jedisPoolConfig() {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		// 最大空闲数
		jedisPoolConfig.setMaxIdle(maxIdle);
		// 连接池的最大连接数
		jedisPoolConfig.setMaxTotal(maxTotal);
		// 最大建立连接等待时间
		jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
		// 最小空闲连接数, 默认0
		jedisPoolConfig.setMinIdle(minIdle);
		// 是否在从池中取出连接前进行检验,如果检验失败,则从池中去除连接并尝试取出另一个
		jedisPoolConfig.setTestOnBorrow(testOnBorrow);
		jedisPoolConfig.setTestOnReturn(testOnReturn);
		jedisPoolConfig.setBlockWhenExhausted(blockWhenExhausted);
		return jedisPoolConfig;
	}

	@Bean
	public RedisSentinelConfiguration redisSentinelConfiguration() {
		RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration();
		redisSentinelConfiguration.setMaster(clsuterName);
		Set<RedisNode> sentinels = new HashSet<>();
		String[] sNodes = nodes.split(",");
		for (String node : sNodes) {
			String[] hostPort = node.split(":");
			String host = hostPort[0];
			int port = Integer.parseInt(hostPort[1]);
			sentinels.add(new RedisNode(host, port));
		}
		redisSentinelConfiguration.setSentinels(sentinels);
		return redisSentinelConfiguration;
	}

	@Product
	@Bean("jedisConnectionFactory")
	public JedisConnectionFactory productJedisConnectionFactory() {
		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisSentinelConfiguration(),
				jedisPoolConfig());
		jedisConnectionFactory.setPassword(password);
		jedisConnectionFactory.setTimeout(timeout);
		return jedisConnectionFactory;
	}

	@Dev
	@Bean("jedisConnectionFactory")
	public JedisConnectionFactory devJedisConnectionFactory(){
		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
		String[] hostpost = nodes.split(":");
		String host= hostpost[0];
		int port = Integer.valueOf(hostpost[1]);
		jedisConnectionFactory.setHostName(host);
		jedisConnectionFactory.setPort(port);
		jedisConnectionFactory.setPassword(password);
		jedisConnectionFactory.setTimeout(timeout);
		jedisConnectionFactory.setPoolConfig(jedisPoolConfig());
		return jedisConnectionFactory;
	}


	/**
	 * 设置 redisTemplate 序列化方式
	 * @param
	 * @return
	 */
	@Bean
	public RedisTemplate<Object, Object> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
		RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(jedisConnectionFactory);
		// 设置值（value）的序列化采用FastJsonRedisSerializer。
		FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
		redisTemplate.setValueSerializer(fastJsonRedisSerializer);
		redisTemplate.setHashValueSerializer(fastJsonRedisSerializer);
		// 设置键（key）的序列化采用StringRedisSerializer。
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		redisTemplate.setDefaultSerializer(fastJsonRedisSerializer);
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}

}
