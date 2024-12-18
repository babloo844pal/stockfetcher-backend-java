package com.stockfetcher.cache;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockfetcher.model.InstitutionalHolderEntity;

@Service
public class InstitutionalHolderRedisService {

    private static final int CACHE_TIMEOUT = 10; // Cache timeout in minutes

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void saveToCache(String key, List<InstitutionalHolderEntity> data) {
        try {
            redisTemplate.opsForValue().set(
                key, objectMapper.writeValueAsString(data), CACHE_TIMEOUT, TimeUnit.MINUTES
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error saving institutional holders to Redis", e);
        }
    }

    public List<InstitutionalHolderEntity> getFromCache(String key) {
        String json = (String) redisTemplate.opsForValue().get(key);
        if (json != null) {
            try {
                return List.of(objectMapper.readValue(json, InstitutionalHolderEntity[].class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error fetching data from Redis", e);
            }
        }
        return null;
    }

    public String getCacheKeyBySymbol(String symbol) {
        return "institutional_holders_" + symbol;
    }

    public String getCacheKeyBySymbolAndExchange(String symbol, String exchange) {
        return "institutional_holders_" + symbol + "_" + exchange;
    }
}
