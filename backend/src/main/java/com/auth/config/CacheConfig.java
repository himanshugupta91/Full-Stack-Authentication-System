package com.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

/**
 * Spring Cache configuration backed by Redis with per-cache TTL controls.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Value("${app.cache.default-ttl-seconds:30}")
    private long defaultCacheTtlSeconds;

    @Value("${app.cache.user-profile.ttl-seconds:60}")
    private long userProfileTtlSeconds;

    @Value("${app.cache.admin-dashboard.ttl-seconds:30}")
    private long adminDashboardTtlSeconds;
    /**
     * Executes redis cache manager builder customizer logic.
     */

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        RedisSerializationContext.SerializationPair<Object> valueSerializationPair = RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer());

        RedisCacheConfiguration baseConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(valueSerializationPair)
                .disableCachingNullValues();

        return cacheManagerBuilder -> cacheManagerBuilder
                .cacheDefaults(baseConfiguration.entryTtl(Duration.ofSeconds(Math.max(1, defaultCacheTtlSeconds))))
                .withCacheConfiguration(
                        CacheNames.USER_PROFILE,
                        baseConfiguration.entryTtl(Duration.ofSeconds(Math.max(1, userProfileTtlSeconds))))
                .withCacheConfiguration(
                        CacheNames.ADMIN_DASHBOARD,
                        baseConfiguration.entryTtl(Duration.ofSeconds(Math.max(1, adminDashboardTtlSeconds))));
    }
}
