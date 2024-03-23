package com.example.jwt.repository;

import java.util.Optional;

public interface RedisAuthRepository {
    Optional<String> get(String key);

    boolean hasKey(String key);

    void save(String key, String value, Long TTL);

    String getAndDelete(String key);
}
