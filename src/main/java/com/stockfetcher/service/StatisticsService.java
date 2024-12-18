package com.stockfetcher.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockfetch.cache.StatisticsRedisService;
import com.stockfetcher.model.MetaEntity;
import com.stockfetcher.model.StatisticsEntity;
import com.stockfetcher.repository.MetaRepository;
import com.stockfetcher.repository.StatisticsRepository;

@Service
public class StatisticsService {

    private final MetaRepository metaRepository;
    private final StatisticsRepository statisticsRepository;
    private final StatisticsRedisService redisService;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
	@Value("${twelve.data.api.key}")
	private String API_KEY;

	@Value("${twelve.data.api.url}")
	private String API_URL;

    public StatisticsService(MetaRepository metaRepository, StatisticsRepository statisticsRepository, StatisticsRedisService redisService, WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.metaRepository = metaRepository;
        this.statisticsRepository = statisticsRepository;
        this.redisService = redisService;
        this.webClient = webClientBuilder.baseUrl("https://api.twelvedata.com").build();
        this.objectMapper = objectMapper;
    }

    public StatisticsEntity getStatistics(String symbol, String apiKey) {
        // Check Redis cache
        StatisticsEntity cachedStatistics = redisService.getStatisticsFromCache(symbol);
        if (cachedStatistics != null) {
            return cachedStatistics;
        }

        // Check database
        StatisticsEntity dbStatistics = statisticsRepository.findBySymbol(symbol);
        if (dbStatistics != null) {
            redisService.saveStatisticsToCache(dbStatistics); // Cache the result
            return dbStatistics;
        }

        // Fetch from API
        String url = UriComponentsBuilder.fromUriString(API_URL + "/statistics?").queryParam("symbol", symbol)
				.queryParam("apikey", API_KEY).build().toString();

		String response = webClient.get().uri(url).retrieve().bodyToMono(String.class).block();

        try {
            JsonNode rootNode = objectMapper.readTree(response);

            // Save meta data
            JsonNode metaNode = rootNode.get("meta");
            MetaEntity metaEntity = objectMapper.treeToValue(metaNode, MetaEntity.class);
            metaRepository.save(metaEntity);

            // Save statistics data
            StatisticsEntity statisticsEntity = new StatisticsEntity();
            statisticsEntity.setSymbol(symbol);
            statisticsEntity.setData(rootNode.get("statistics").toString());
            statisticsRepository.save(statisticsEntity);

            // Save to cache
            redisService.saveStatisticsToCache(statisticsEntity);

            return statisticsEntity;
        } catch (Exception e) {
            throw new RuntimeException("Error processing API response: " + e.getMessage(), e);
        }
    }
}
