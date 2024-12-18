package com.stockfetch.cache;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockfetcher.model.IncomeStatementEntity;

@Service
public class IncomeStatementRedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String getCacheKey(String symbol, String fiscalDate) {
        return "income_statement_" + symbol + "_" + fiscalDate;
    }

    public void saveIncomeStatementToCache(IncomeStatementEntity incomeStatement) {
        try {
            String cacheKey = getCacheKey(incomeStatement.getSymbol(), incomeStatement.getFiscalDate());
            redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(incomeStatement), 10, TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing IncomeStatementEntity for Redis: " + e.getMessage());
        }
    }

    public IncomeStatementEntity getIncomeStatementFromCache(String symbol, String fiscalDate) {
        String cacheKey = getCacheKey(symbol, fiscalDate);
        String data = (String) redisTemplate.opsForValue().get(cacheKey);

        if (data != null) {
            try {
                return objectMapper.readValue(data, IncomeStatementEntity.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error deserializing IncomeStatementEntity from Redis: " + e.getMessage());
            }
        }
        return null;
    }
}
