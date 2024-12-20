package com.stockfetcher.cache;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GenericRedisService {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * Store data in Redis with a specified key and TTL.
	 *
	 * @param key  The Redis key.
	 * @param data The data to store.
	 * @param ttl  Time-To-Live in minutes (if null, no expiration is set).
	 */
	public void save(String key, Object data, Long ttl) {
		try {
			String value = objectMapper.writeValueAsString(data);
			if (ttl != null) {
				redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.MINUTES);
			} else {
				redisTemplate.opsForValue().set(key, value);
			}
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error serializing data for Redis key: " + key, e);
		}
	}

	/**
	 * Retrieve data from Redis by key.
	 *
	 * @param key       The Redis key.
	 * @param valueType The expected type of the stored data.
	 * @param <T>       The type parameter for the data.
	 * @return The stored data or null if the key does not exist.
	 */
	public <T> T get(String key, Class<T> valueType) {
		try {
			String value = redisTemplate.opsForValue().get(key);
			if (value != null) {
				return objectMapper.readValue(value, valueType);
			}
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error deserializing data for Redis key: " + key, e);
		}
		return null;
	}

	/**
	 * Delete data from Redis by key.
	 *
	 * @param key The Redis key.
	 */
	public void delete(String key) {
		redisTemplate.delete(key);
	}

	/**
	 * Check if a key exists in Redis.
	 *
	 * @param key The Redis key.
	 * @return True if the key exists, false otherwise.
	 */
	public boolean exists(String key) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(key));
	}

	/**
	 * Save search results to Redis.
	 *
	 * @param prefix The search prefix.
	 * @param stocks The list of stock entities to cache.
	 */
	public void saveSearchResults(String key, String prefix, List<?> stocks, Long ttl) {
		try {
			key = key + prefix.toLowerCase();
			String value = objectMapper.writeValueAsString(stocks);
			redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.MINUTES);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error serializing search results for Redis", e);
		}
	}

	/**
	 * Fetch search results from Redis.
	 *
	 * @param prefix    The search prefix.
	 * @param valueType The expected type of the cached data.
	 * @param <T>       The type of the data.
	 * @return The cached search results, or null if not found.
	 */
	public <T> List<T> getSearchResults(String key, String prefix, Class<T[]> valueType) {
		try {
			key = key + prefix.toLowerCase();
			String value = redisTemplate.opsForValue().get(key);
			if (value != null) {
				return List.of(objectMapper.readValue(value, valueType));
			}
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error deserializing search results from Redis", e);
		}
		return null;
	}

}
