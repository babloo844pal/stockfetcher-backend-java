package com.stockfetch.cache;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockfetcher.model.MarketEntity;

@Service
public class MarketRedisService {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	private static final String ALL_MARKETS_CACHE_KEY = "all_markets";
	private static final String COUNTRY_MARKETS_CACHE_KEY_PREFIX = "markets_country_";
	private static final String COUNTRY_NAME_CACHE_KEY_PREFIX = "markets_country_name_";
	private static final String COUNTRY_NAME_CODE_CACHE_KEY_PREFIX = "markets_country_name_code_";

	public void saveAllMarketsToCache(List<MarketEntity> markets) {
		try {
			redisTemplate.opsForValue().set(ALL_MARKETS_CACHE_KEY, objectMapper.writeValueAsString(markets), 10,
					TimeUnit.MINUTES);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error serializing market data for Redis", e);
		}
	}

	public List<MarketEntity> getAllMarketsFromCache() {
		return getMarketsFromCache(ALL_MARKETS_CACHE_KEY);
	}

	public void saveMarketsByCountryToCache(String country, List<MarketEntity> markets) {
		try {
			String cacheKey = COUNTRY_MARKETS_CACHE_KEY_PREFIX + country.toLowerCase();
			redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(markets), 10, TimeUnit.MINUTES);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error serializing market data for Redis", e);
		}
	}

	public List<MarketEntity> getMarketsByCountryFromCache(String country) {
		String cacheKey = COUNTRY_MARKETS_CACHE_KEY_PREFIX + country.toLowerCase();
		return getMarketsFromCache(cacheKey);
	}

	public void saveMarketsByCountryAndNameToCache(String country,String name, List<MarketEntity> markets) {
		try {
			String cacheKey = COUNTRY_NAME_CACHE_KEY_PREFIX + country.toLowerCase()+"_"+name.toLowerCase();
			redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(markets), 10, TimeUnit.MINUTES);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error serializing market data for Redis", e);
		}
	}
	
	public List<MarketEntity> getMarketsByCountryAndNameFromCache(String country,String name) {
		String cacheKey = COUNTRY_NAME_CACHE_KEY_PREFIX + country.toLowerCase()+"_"+name.toLowerCase();
		return getMarketsFromCache(cacheKey);
	}
	
	
	public void saveMarketsByCountryAndNameAndCodeToCache(String country,String name, String code ,List<MarketEntity> markets) {
		try {
			String cacheKey = COUNTRY_NAME_CODE_CACHE_KEY_PREFIX + country.toLowerCase()+"_"+name.toLowerCase()+"_"+code.toLowerCase();
			redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(markets), 10, TimeUnit.MINUTES);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error serializing market data for Redis", e);
		}
	}
	
	public List<MarketEntity> getMarketsByCountryAndNameAndCodeFromCache(String country,String name, String code) {
		String cacheKey = COUNTRY_NAME_CODE_CACHE_KEY_PREFIX + country.toLowerCase()+"_"+name.toLowerCase()+"_"+code.toLowerCase();
		return getMarketsFromCache(cacheKey);
	}
	
	private List<MarketEntity> getMarketsFromCache(String cacheKey) {
		String data = (String) redisTemplate.opsForValue().get(cacheKey);
		if (data != null) {
			try {
				return List.of(objectMapper.readValue(data, MarketEntity[].class));
			} catch (JsonProcessingException e) {
				throw new RuntimeException("Error deserializing market data from Redis", e);
			}
		}
		return null;
	}
}
