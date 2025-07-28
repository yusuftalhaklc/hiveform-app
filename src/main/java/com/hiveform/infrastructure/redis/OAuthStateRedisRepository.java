package com.hiveform.infrastructure.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class OAuthStateRedisRepository {
    private static final String PREFIX = "oauth_state:";
    private static final long TTL_MINUTES = 10;
    private final RedisTemplate<String, String> redisTemplate;

    public OAuthStateRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveState(String state) {
        redisTemplate.opsForValue().set(PREFIX + state, "valid", TTL_MINUTES, TimeUnit.MINUTES);
    }

    public boolean isValidState(String state) {
        return redisTemplate.hasKey(PREFIX + state);
    }

    public void deleteState(String state) {
        redisTemplate.delete(PREFIX + state);
    }
} 