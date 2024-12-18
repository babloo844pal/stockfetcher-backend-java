package com.stockfetch.cache;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockfetcher.model.RecommendationsEntity;

@Service
public class RecommendationsRedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void saveToCache(String key, RecommendationsEntity data) {
        try {
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(data), 10, TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing Recommendations for Redis", e);
        }
    }

    public RecommendationsEntity getFromCache(String key) {
        String json = (String) redisTemplate.opsForValue().get(key);
        if (json != null) {
            try {
                return objectMapper.readValue(json, RecommendationsEntity.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error deserializing Recommendations from Redis", e);
            }
        }
        return null;
    }
}
