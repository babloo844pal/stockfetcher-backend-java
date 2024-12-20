package com.stockfetcher.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceLoader {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void saveToCache(String key, Object data, long ttlMinutes) {
        redisTemplate.opsForValue().set(key, data, ttlMinutes, TimeUnit.MINUTES);
    }

    public Object getFromCache(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
