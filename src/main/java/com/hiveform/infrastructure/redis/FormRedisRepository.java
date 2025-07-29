package com.hiveform.infrastructure.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class FormRedisRepository {
    private static final String PREFIX = "form:shortlink:";
    private final RedisTemplate<String, Object> redisTemplate;

    public FormRedisRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveFormByShortlink(String shortlink, Object form, long ttlMinutes) {
        redisTemplate.opsForValue().set(PREFIX + shortlink, form, ttlMinutes, TimeUnit.MINUTES);
    }

    public Object getFormByShortlink(String shortlink) {
        return redisTemplate.opsForValue().get(PREFIX + shortlink);
    }

    public void deleteFormByShortlink(String shortlink) {
        redisTemplate.delete(PREFIX + shortlink);
    }

}
