package com.auth.service.support;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Redis-backed fixed-window rate limiting helper.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimitService {

    private final StringRedisTemplate redisTemplate;

    /** Consumes one request token and returns allowance metadata. */
    public RateLimitDecision consume(String key, long limit, Duration window) {
        if (limit <= 0 || window == null || window.isNegative() || window.isZero()) {
            return new RateLimitDecision(true, -1, 0, limit);
        }

        try {
            Long currentCount = redisTemplate.opsForValue().increment(key);
            if (currentCount == null) {
                return new RateLimitDecision(true, -1, 0, limit);
            }

            if (currentCount == 1) {
                redisTemplate.expire(key, window);
            }

            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            long retryAfterSeconds = ttl == null || ttl < 0 ? window.getSeconds() : ttl;
            boolean allowed = currentCount <= limit;
            return new RateLimitDecision(allowed, retryAfterSeconds, currentCount, limit);
        } catch (Exception exception) {
            // Fail open on Redis outages to avoid full auth downtime.
            log.warn("Rate limiting unavailable for key={}", key, exception);
            return new RateLimitDecision(true, -1, 0, limit);
        }
    }

    public record RateLimitDecision(boolean allowed, long retryAfterSeconds, long count, long limit) {
    }
}
