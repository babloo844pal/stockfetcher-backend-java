package com.stockfetcher.cache;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockfetcher.model.StatisticsEntity;

@Service
public class StatisticsRedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String getCacheKey(String symbol) {
        return "statistics_" + symbol;
    }

    public void saveStatisticsToCache(StatisticsEntity statisticsEntity) {
        try {
            String cacheKey = getCacheKey(statisticsEntity.getSymbol());
            redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(statisticsEntity), 10, TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing statistics entity for Redis: " + e.getMessage());
        }
    }

    public StatisticsEntity getStatisticsFromCache(String symbol) {
        String cacheKey = getCacheKey(symbol);
        String data = (String) redisTemplate.opsForValue().get(cacheKey);

        if (data != null) {
            try {
                return objectMapper.readValue(data, StatisticsEntity.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error deserializing statistics entity from Redis: " + e.getMessage());
            }
        }
        return null;
    }
}
