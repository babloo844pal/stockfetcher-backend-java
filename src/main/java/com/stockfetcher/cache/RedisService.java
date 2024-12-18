package com.stockfetcher.cache;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.stockfetcher.model.StockPrice;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void saveStockPrice(StockPrice stockPrice) {
        String cacheKey = getCacheKey(stockPrice.getSymbol(), stockPrice.getIntervalSlice(), stockPrice.getDatetime());
        redisTemplate.opsForValue().set(cacheKey, stockPrice);
    }

    public StockPrice getStockPrice(String symbol, String intervalSlice, LocalDateTime datetime) {
        String cacheKey = getCacheKey(symbol, intervalSlice, datetime);
        return (StockPrice) redisTemplate.opsForValue().get(cacheKey);
    }

    private String getCacheKey(String symbol, String intervalSlice, LocalDateTime datetime) {
        return symbol + "_" + intervalSlice + "_" + datetime.toString();
    }
}
