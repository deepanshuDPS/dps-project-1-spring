package com.indower.indtest.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import com.indower.indtest.utils.AppConstants;
import com.indower.indtest.utils.EnvironmentSetup;

@Configuration
@EnableRedisRepositories()
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private Integer port;

    @Autowired
    private EnvironmentSetup setup;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        System.out.println("-----> What is it: "+setup.isProd());
        if (setup.isProd()) {
            RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
            config.setHostName(host);
            config.setPort(port);
            return new JedisConnectionFactory(config);
        }
        return new JedisConnectionFactory();
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
