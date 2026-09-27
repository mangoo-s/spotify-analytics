package com.example.spotifyscrobble.catalog.configs;

import com.example.spotifyscrobble.catalog.entity.ArtistEntity;
import com.example.spotifyscrobble.catalog.entity.TrackEntity;
import com.example.spotifyscrobble.catalog.internalDto.ArtistCacheView;
import com.example.spotifyscrobble.catalog.internalDto.TrackCacheView;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;


import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CatalogRedisCacheConfig {

    @Bean
    public RedisCacheManagerBuilderCustomizer catalogCacheCustomizer() {
        JacksonJsonRedisSerializer<ArtistCacheView> artistSerializer =
                new JacksonJsonRedisSerializer<>(ArtistCacheView.class);
        JacksonJsonRedisSerializer<TrackCacheView> trackSerializer =
                new JacksonJsonRedisSerializer<>(TrackCacheView.class);

        return builder -> builder
                .withCacheConfiguration("artistCache", RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(15))
                        .disableCachingNullValues()
                        .serializeKeysWith(RedisSerializationContext.SerializationPair
                                .fromSerializer(new StringRedisSerializer()))
                        .serializeValuesWith(RedisSerializationContext.SerializationPair
                                .fromSerializer(artistSerializer)))
                .withCacheConfiguration("trackCache", RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(15))
                        .disableCachingNullValues()
                        .serializeKeysWith(RedisSerializationContext.SerializationPair
                                .fromSerializer(new StringRedisSerializer()))
                        .serializeValuesWith(RedisSerializationContext.SerializationPair
                                .fromSerializer(trackSerializer)));
    }

}
