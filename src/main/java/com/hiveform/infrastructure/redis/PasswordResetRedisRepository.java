package com.hiveform.infrastructure.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class PasswordResetRedisRepository {
    private static final String PREFIX = "password_reset:";
    private static final long TTL_MINUTES = 3;
    private final RedisTemplate<String, String> redisTemplate;

    public PasswordResetRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveResetToken(String email, String resetToken) {
        // Store token with email as key
        redisTemplate.opsForValue().set(PREFIX + email, resetToken, TTL_MINUTES, TimeUnit.MINUTES);
        // Also store email with token as key for reverse lookup
        redisTemplate.opsForValue().set(PREFIX + "token:" + resetToken, email, TTL_MINUTES, TimeUnit.MINUTES);
    }

    public String getResetToken(String email) {
        return redisTemplate.opsForValue().get(PREFIX + email);
    }

    public String getEmailByToken(String token) {
        return redisTemplate.opsForValue().get(PREFIX + "token:" + token);
    }

    public void deleteResetToken(String email) {
        String token = getResetToken(email);
        if (token != null) {
            redisTemplate.delete(PREFIX + "token:" + token);
        }
        redisTemplate.delete(PREFIX + email);
    }

    public boolean hasResetToken(String email) {
        return redisTemplate.hasKey(PREFIX + email);
    }

    public boolean isValidToken(String token) {
        return redisTemplate.hasKey(PREFIX + "token:" + token);
    }
} 