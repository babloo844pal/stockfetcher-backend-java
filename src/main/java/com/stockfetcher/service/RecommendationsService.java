package com.stockfetcher.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockfetcher.cache.RecommendationsRedisService;
import com.stockfetcher.model.RecommendationsEntity;
import com.stockfetcher.repository.RecommendationsRepository;

@Service
public class RecommendationsService {

	private final RecommendationsRepository repository;
	private final RecommendationsRedisService redisService;
	private final WebClient webClient;
	private final ObjectMapper objectMapper;

	@Value("${twelve.data.api.key}")
	private String API_KEY;

	@Value("${twelve.data.api.url}")
	private String API_URL;

	public RecommendationsService(RecommendationsRepository repository, RecommendationsRedisService redisService,
			WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
		this.repository = repository;
		this.redisService = redisService;
		this.webClient = webClientBuilder.baseUrl("https://api.twelvedata.com").build();
		this.objectMapper = objectMapper;
	}

	public RecommendationsEntity getRecommendations(String symbol) {
		String cacheKey = "recommendations_" + symbol;
		RecommendationsEntity cachedData = redisService.getFromCache(cacheKey);
		if (cachedData != null)
			return cachedData;

		RecommendationsEntity dbData = repository.findBySymbol(symbol);
		if (dbData != null) {
			redisService.saveToCache(cacheKey, dbData);
			return dbData;
		}

		return fetchAndSaveFromApi(symbol, cacheKey);
	}

	private RecommendationsEntity fetchAndSaveFromApi(String symbol, String cacheKey) {
		String url = UriComponentsBuilder.fromUriString(API_URL + "/recommendations").queryParam("symbol", symbol)
				.queryParam("apikey", API_KEY).queryParam("source", "docs").build().toString();

		String response = webClient.get().uri(url).retrieve().bodyToMono(String.class).block();

		try {
			JsonNode rootNode = objectMapper.readTree(response);

			RecommendationsEntity entity = new RecommendationsEntity();
			entity.setSymbol(rootNode.path("meta").path("symbol").asText());
			entity.setName(rootNode.path("meta").path("name").asText());
			entity.setCurrency(rootNode.path("meta").path("currency").asText());
			entity.setExchange(rootNode.path("meta").path("exchange").asText());
			entity.setMicCode(rootNode.path("meta").path("mic_code").asText());
			entity.setExchangeTimezone(rootNode.path("meta").path("exchange_timezone").asText());
			entity.setType(rootNode.path("meta").path("type").asText());

			// Set trends JSON
			entity.setTrends(rootNode.path("trends"));

			entity.setRating(rootNode.path("rating").asDouble());
			entity.setStatus(rootNode.path("status").asText());

			repository.save(entity);
			redisService.saveToCache(cacheKey, entity);
			return entity;

		} catch (Exception e) {
			throw new RuntimeException("Error fetching Recommendations data from API", e);
		}
	}
}
