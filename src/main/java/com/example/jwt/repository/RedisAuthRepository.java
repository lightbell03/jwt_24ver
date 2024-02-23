package com.example.jwt.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisAuthRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void save(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public boolean isBannedToken(String key) {
        return redisTemplate.opsForHash().hasKey("ban", key);
    }

    public void banToken(String key, String value) {
        redisTemplate.opsForHash().put("ban", key, value);
    }
}
