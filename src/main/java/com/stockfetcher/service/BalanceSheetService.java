package com.stockfetcher.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockfetch.cache.BalanceSheetRedisService;
import com.stockfetcher.model.BalanceSheetEntity;
import com.stockfetcher.repository.BalanceSheetRepository;

@Service
public class BalanceSheetService {

    private final BalanceSheetRepository repository;
    private final BalanceSheetRedisService redisService;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

	@Value("${twelve.data.api.key}")
	private String API_KEY;

	@Value("${twelve.data.api.url}")
	private String API_URL;

    public BalanceSheetService(BalanceSheetRepository repository, BalanceSheetRedisService redisService,
                               WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.repository = repository;
        this.redisService = redisService;
        this.webClient = webClientBuilder.baseUrl("https://api.twelvedata.com").build();
        this.objectMapper = objectMapper;
    }

    public List<BalanceSheetEntity> getBySymbol(String symbol) {
        String cacheKey = redisService.getCacheKeyBySymbol(symbol);

        // Check Redis Cache
        List<BalanceSheetEntity> cachedData = redisService.getFromCache(cacheKey);
        if (cachedData != null) return cachedData;

        // Check Database
        List<BalanceSheetEntity> dbData = repository.findBySymbol(symbol);
        if (!dbData.isEmpty()) {
            redisService.saveToCache(cacheKey, dbData);
            return dbData;
        }

        // Fetch from API
        return fetchFromApi(symbol, cacheKey);
    }

    public List<BalanceSheetEntity> getBySymbolAndExchange(String symbol, String exchange) {
        String cacheKey = redisService.getCacheKeyBySymbolAndExchange(symbol, exchange);

        // Check Redis Cache
        List<BalanceSheetEntity> cachedData = redisService.getFromCache(cacheKey);
        if (cachedData != null) return cachedData;

        // Check Database
        List<BalanceSheetEntity> dbData = repository.findBySymbolAndExchange(symbol, exchange);
        if (!dbData.isEmpty()) {
            redisService.saveToCache(cacheKey, dbData);
            return dbData;
        }

        // Fetch from API
        return fetchFromApi(symbol, cacheKey);
    }

    private List<BalanceSheetEntity> fetchFromApi(String symbol, String cacheKey) {
        // Fetch from API
        String url = UriComponentsBuilder.fromUriString(API_URL + "/balance_sheet?").queryParam("symbol", symbol)
				.queryParam("apikey", API_KEY).queryParam("source", "docs").build().toString();
		String response = webClient.get().uri(url).retrieve().bodyToMono(String.class).block();
        try {
            JsonNode rootNode = objectMapper.readTree(response);
            List<BalanceSheetEntity> entities = objectMapper.convertValue(rootNode.path("balance_sheet"),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, BalanceSheetEntity.class));

            repository.saveAll(entities);
            redisService.saveToCache(cacheKey, entities);
            return entities;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching data from API", e);
        }
    }
}
