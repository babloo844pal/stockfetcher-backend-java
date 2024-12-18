package com.stockfetch.cache;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockfetcher.model.TechnicalIndicatorsData;

@Service
public class TechnicalIndicatorsRedisService {

    private static final String CACHE_KEY = "transform_data";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void saveToCache(List<TechnicalIndicatorsData> data) {
        try {
            redisTemplate.opsForValue().set(CACHE_KEY, objectMapper.writeValueAsString(data), 10, TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing TransformData for Redis", e);
        }
    }
    

    public List<TechnicalIndicatorsData> getFromCache() {
        String json = (String) redisTemplate.opsForValue().get(CACHE_KEY);
        if (json != null) {
            try {
                return List.of(objectMapper.readValue(json, TechnicalIndicatorsData[].class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error deserializing TransformData from Redis", e);
            }
        }
        return null;
    }

}
