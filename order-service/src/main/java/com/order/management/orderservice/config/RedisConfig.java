package com.order.management.orderservice.config;

import com.order.management.orderservice.dto.validation.ValidationResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import java.util.UUID;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<UUID, ValidationResult> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<UUID, ValidationResult> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
