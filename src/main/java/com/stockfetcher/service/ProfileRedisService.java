package com.stockfetcher.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.stockfetcher.model.StockProfile;

@Service
public class ProfileRedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Saves a StockProfile object to Redis cache with a custom key.
     *
     * @param stockProfile The StockProfile object to be cached.
     */
    public void saveStockProfile(StockProfile stockProfile) {
        String cacheKey = getCacheKey(stockProfile.getSymbol());
        redisTemplate.opsForValue().set(cacheKey, stockProfile, 10, TimeUnit.MINUTES); // Cache for 10 minutes
    }

    /**
     * Retrieves a StockProfile object from Redis cache.
     *
     * @param symbol The stock symbol used as a cache key.
     * @return The cached StockProfile object, or null if not found.
     */
    public StockProfile getStockProfile(String symbol) {
        String cacheKey = getCacheKey(symbol);
        return (StockProfile) redisTemplate.opsForValue().get(cacheKey);
    }

    /**
     * Generates a cache key for a StockProfile based on its symbol.
     *
     * @param symbol The stock symbol.
     * @return The cache key string.
     */
    private String getCacheKey(String symbol) {
        return "stock_profile_" + symbol;
    }
}
