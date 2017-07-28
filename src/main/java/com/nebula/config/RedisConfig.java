package com.nebula.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.PostConstruct;

/**
 * Created by hzq on 2017/7/27.
 */
@Configuration
public class RedisConfig {

	private final static Logger logger = LoggerFactory.getLogger(RedisConfig.class);

	@Value("${redis.sentinel.default.master_name}")
	private String defaultMasterName;

	@Value("${redis.sentinel.default.hosts}")
	private String defaultHosts;

	@PostConstruct
	public void redisConfigLogger(){
		logger.info("======================================");
		logger.info("=========执行 RedisConfig中bean的初始化=================");
		logger.info("======================================");
	}

	@Bean
	public StringRedisTemplate stringRedisTemplate(){
		StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
		stringRedisTemplate.setDefaultSerializer(new StringRedisSerializer());
		stringRedisTemplate.setEnableDefaultSerializer(true);
		stringRedisTemplate.setConnectionFactory(jedisConnectionFactory());

		return stringRedisTemplate;
	}

	@Bean
	public RedisConnectionFactory jedisConnectionFactory(){
		return new JedisConnectionFactory(defaultRedisSentinelConfiguration(), jedisPoolConfig());
	}

	@Bean
	public RedisSentinelConfiguration defaultRedisSentinelConfiguration(){
		return getRedisSentinelConfiguration(defaultMasterName, defaultHosts);
	}

	public RedisSentinelConfiguration getRedisSentinelConfiguration(String masterName, String hosts) {
		logger.info("-------- sentinel server :\t" + masterName + "\t" + hosts);

		RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration().master(masterName);

		String[] array = hosts.trim().split("\\|");
		if(array != null && array.length > 0){
			for(String element : array){
				String[] elementArray = element.split(":");

				if(elementArray != null && elementArray.length > 1){
					redisSentinelConfiguration.sentinel(elementArray[0].trim(), Integer.valueOf(elementArray[1].trim()));
				}
			}
		}

		return redisSentinelConfiguration;
	}

	/**
	 * 配置jedis连接池
	 */
	@Bean
	public JedisPoolConfig jedisPoolConfig(){
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

		//最大活动对象数
		jedisPoolConfig.setMaxTotal(2000);
		//最大能够保持idel状态的对象数
		jedisPoolConfig.setMaxIdle(100);
		//最小能够保持idel状态的对象数
		jedisPoolConfig.setMinIdle(10);
		//当池内没有返回对象时，最大等待时间
		jedisPoolConfig.setMaxWaitMillis(10000);

		//“空闲链接”检测线程，检测的周期，毫秒数。如果为负值，表示不运行“检测线程”。默认为-1.
		jedisPoolConfig.setTimeBetweenEvictionRunsMillis(30000);

		//当调用borrow Object方法时，是否进行有效性检查
		jedisPoolConfig.setTestOnBorrow(false);
		//当调用return Object方法时，是否进行有效性检查
		jedisPoolConfig.setTestOnReturn(false);
		//向调用者输出“链接”对象时，是否检测它的空闲超时；
		jedisPoolConfig.setTestWhileIdle(true);
		return jedisPoolConfig;
	}


}
