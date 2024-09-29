package com.crane.usercenterback.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * redis配置类
 *
 * @Author Crane Resigned
 * @Date 2024/7/25 17:16:06
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedisConfig {

    private String host;
    private String port;

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());
        template.setValueSerializer(jackson2JsonRedisSerializer);
        return template;
    }

    /**
     * 创建redisson客户端以实现分布式锁
     *
     * @Author CraneResigned
     * @Date 2024/9/29 11:27
     **/
    @Bean
    public RedissonClient redisson() {
        Config config = new Config();
        String redisAddress = String.format("redis://%s:%s", host, port);
        config.useSingleServer().setAddress(redisAddress).setDatabase(1);
        return Redisson.create(config);
    }

}