package com.example.spotifyscrobble.statistics.configs;

import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class StatisticsCacheConfig {

    @Bean
    public RedisCacheManagerBuilderCustomizer statisticsCacheCustomizer(){
        JacksonJsonRedisSerializer<Boolean> valueSerializer = new JacksonJsonRedisSerializer<>(boolean.class);

        return builder -> builder
                .withCacheConfiguration("isListener", RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(8))
                        .disableCachingNullValues()
                        .serializeValuesWith(RedisSerializationContext.SerializationPair
                                .fromSerializer(valueSerializer))
                        .serializeKeysWith(RedisSerializationContext.SerializationPair
                                .fromSerializer(new StringRedisSerializer()))
                );
    }
}
