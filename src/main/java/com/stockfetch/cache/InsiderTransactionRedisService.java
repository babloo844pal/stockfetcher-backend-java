package com.stockfetch.cache;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockfetcher.model.InsiderTransactionEntity;

@Service
public class InsiderTransactionRedisService {

    private static final int CACHE_TIMEOUT = 10; // Cache timeout in minutes

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Save insider transactions to Redis cache.
     */
    public void saveToCache(String key, List<InsiderTransactionEntity> data) {
        try {
            redisTemplate.opsForValue().set(
                key, objectMapper.writeValueAsString(data), CACHE_TIMEOUT, TimeUnit.MINUTES
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error saving insider transactions to Redis", e);
        }
    }

    /**
     * Retrieve insider transactions from Redis cache.
     */
    public List<InsiderTransactionEntity> getFromCache(String key) {
        String json = (String) redisTemplate.opsForValue().get(key);
        if (json != null) {
            try {
                return List.of(
                    objectMapper.readValue(json, InsiderTransactionEntity[].class)
                );
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error reading insider transactions from Redis", e);
            }
        }
        return null;
    }

    /**
     * Generate cache key based on symbol.
     */
    public String getCacheKeyBySymbol(String symbol) {
        return "insider_transactions_" + symbol;
    }

    /**
     * Generate cache key based on symbol and exchange.
     */
    public String getCacheKeyBySymbolAndExchange(String symbol, String exchange) {
        return "insider_transactions_" + symbol + "_" + exchange;
    }
}
