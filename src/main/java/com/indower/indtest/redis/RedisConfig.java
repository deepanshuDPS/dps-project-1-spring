package com.indower.indtest.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories()
public class RedisConfig {

    // @Bean
    // JedisConnectionFactory jedisConnectionFactory() {
    //     JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
    //     return jedisConnectionFactory;
    // }

    @Value("${spring.redis.host}")
	private String host;
	@Value("${spring.redis.port}")
	private Integer port;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        return new JedisConnectionFactory(config);
    }

    @Bean
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(jedisConnectionFactory());
        // template.setKeySerializer(new StringRedisSerializer());
        // template.setHashKeySerializer(new StringRedisSerializer());
        // template.setHashKeySerializer(new JdkSerializationRedisSerializer());
        // template.setValueSerializer(new JdkSerializationRedisSerializer());
        // template.setEnableTransactionSupport(true);
        // template.afterPropertiesSet();
		return template;
	}

    @Bean
	public SetOperations<String, Object> redisSetTemplate() {
		return redisTemplate().opsForSet();
	}
}
