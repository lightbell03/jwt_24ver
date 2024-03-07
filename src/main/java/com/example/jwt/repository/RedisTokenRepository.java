package com.example.jwt.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@Slf4j
@RequiredArgsConstructor
public class RedisTokenRepository implements RedisAuthRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void save(String key, String value, Long TTL) {
        redisTemplate.opsForValue().set(key, value, TTL, TimeUnit.MILLISECONDS);
    }


    public Optional<String> get(String key) {
        String value = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(value);
    }

    @Override
    public String getAndDelete(String key) {
        return redisTemplate.opsForValue().getAndDelete(key);
    }
}
