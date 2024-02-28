package com.example.jwt.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RedisBlackListRepository implements RedisAuthRepository {
    private static final String BAN_PREFIX = "ban:";
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public Optional<String> get(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(BAN_PREFIX + key));
    }

    @Override
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BAN_PREFIX + key));
    }

    @Override
    public void save(String key, String value, Long TTL) {
        redisTemplate.opsForValue().set(BAN_PREFIX + key, value, TTL, TimeUnit.MILLISECONDS);
    }

    @Override
    public String getAndDelete(String key) {
        return redisTemplate.opsForValue().getAndDelete(BAN_PREFIX + key);
    }
}
