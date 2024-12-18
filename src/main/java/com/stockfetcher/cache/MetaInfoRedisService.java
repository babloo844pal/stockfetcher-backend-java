package com.stockfetcher.cache;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockfetcher.model.MetaInfoEntity;

@Service
public class MetaInfoRedisService {

    private static final int CACHE_TIMEOUT = 10; // Cache expiration in minutes

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void saveToCache(String key, MetaInfoEntity data) {
        try {
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(data), CACHE_TIMEOUT, TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error saving MetaInfo to Redis", e);
        }
    }

    public MetaInfoEntity getFromCache(String key) {
        String json = (String) redisTemplate.opsForValue().get(key);
        if (json != null) {
            try {
                return objectMapper.readValue(json, MetaInfoEntity.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error fetching MetaInfo from Redis", e);
            }
        }
        return null;
    }

    public String getCacheKey(String symbol) {
        return "meta_info_" + symbol;
    }
}
