package com.stockfetcher.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockfetch.cache.MarketMoverRedisService;
import com.stockfetcher.model.MarketMoverEntity;
import com.stockfetcher.repository.MarketMoverRepository;

@Service
public class MarketMoverService {

    private final MarketMoverRepository repository;
    private final MarketMoverRedisService redisService;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    
	@Value("${twelve.data.api.key}")
	private String API_KEY;

	@Value("${twelve.data.api.url}")
	private String API_URL;

    public MarketMoverService(MarketMoverRepository repository, MarketMoverRedisService redisService,
                              WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.repository = repository;
        this.redisService = redisService;
        this.webClient = webClientBuilder.baseUrl("https://api.twelvedata.com").build();
        this.objectMapper = objectMapper;
    }

    public List<MarketMoverEntity> getAllData() {
        List<MarketMoverEntity> cachedData = redisService.getFromCache();
        if (cachedData != null) return cachedData;

        List<MarketMoverEntity> dbData = repository.findAll();
        if (!dbData.isEmpty()) {
            redisService.saveToCache(dbData);
            return dbData;
        }

        return fetchAndSaveFromApi();
    }

    private List<MarketMoverEntity> fetchAndSaveFromApi() {
         // Fetch from API
        String url = UriComponentsBuilder.fromUriString(API_URL + "/market_movers/stocks?")
				.queryParam("apikey", API_KEY).build().toString();

		String response = webClient.get().uri(url).retrieve().bodyToMono(String.class).block();

        try {
            JsonNode rootNode = objectMapper.readTree(response).get("values");
            List<MarketMoverEntity> data = objectMapper.readValue(rootNode.toString(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, MarketMoverEntity.class));
            repository.saveAll(data);
            redisService.saveToCache(data);
            return data;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching Market Mover data from API", e);
        }
    }
}
