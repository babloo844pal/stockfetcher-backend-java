package com.stockfetch.cache;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockfetcher.model.StockEntity;

@Service
public class StockRedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String CACHE_KEY = "all_stocks";

    public void saveAllStocksToCache(List<StockEntity> stocks) {
        try {
            redisTemplate.opsForValue().set(CACHE_KEY, objectMapper.writeValueAsString(stocks), 10, TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing stocks data for Redis: " + e.getMessage());
        }
    }

    public List<StockEntity> getAllStocksFromCache() {
        String data = (String) redisTemplate.opsForValue().get(CACHE_KEY);
        if (data != null) {
            try {
                return List.of(objectMapper.readValue(data, StockEntity[].class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error deserializing stocks data from Redis: " + e.getMessage());
            }
        }
        return null;
    }
}
