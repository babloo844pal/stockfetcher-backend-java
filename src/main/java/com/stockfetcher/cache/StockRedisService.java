package com.stockfetcher.cache;

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
	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	private static final String CACHE_KEY = "all_stocks";

	private static final String CACHE_KEY_PREFIX = "stock_search:";
	private static final int CACHE_TTL = 30; // Cache for 30 minutes

	public void saveAllStocksToCache(List<StockEntity> stocks) {
		try {
			redisTemplate.opsForValue().set(CACHE_KEY, objectMapper.writeValueAsString(stocks), 10, TimeUnit.MINUTES);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error serializing stocks data for Redis: " + e.getMessage());
		}
	}

	public void saveToCache(String key, String prefix, List<StockEntity> stocks) {
		try {
			key = key + prefix.toLowerCase();
			String value = objectMapper.writeValueAsString(stocks);
			redisTemplate.opsForValue().set(key, value, CACHE_TTL, TimeUnit.MINUTES);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error serializing stock search results for Redis", e);
		}
	}

	public List<StockEntity> getAllStocksFromCache() {
		String data = redisTemplate.opsForValue().get(CACHE_KEY);
		if (data != null) {
			try {
				return List.of(objectMapper.readValue(data, StockEntity[].class));
			} catch (JsonProcessingException e) {
				throw new RuntimeException("Error deserializing stocks data from Redis: " + e.getMessage());
			}
		}
		return null;
	}

	public List<StockEntity> getFromCache(String prefix) {
		try {
			String key = CACHE_KEY_PREFIX + prefix.toLowerCase();
			String value = redisTemplate.opsForValue().get(key);
			if (value != null) {
				return List.of(objectMapper.readValue(value, StockEntity[].class));
			}
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error deserializing stock search results from Redis", e);
		}
		return null;
	}
}
