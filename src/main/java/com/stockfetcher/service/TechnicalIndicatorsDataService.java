package com.stockfetcher.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockfetch.cache.TechnicalIndicatorsRedisService;
import com.stockfetcher.model.TechnicalIndicatorsData;
import com.stockfetcher.repository.TechnicalIndicatorsDataRepository;

@Service
public class TechnicalIndicatorsDataService {

	private final TechnicalIndicatorsDataRepository repository;
	private final TechnicalIndicatorsRedisService redisService;
	private final WebClient webClient;
	private final ObjectMapper objectMapper;

	@Value("${twelve.data.api.key}")
	private String API_KEY;

	@Value("${twelve.data.api.url}")
	private String API_URL;

	public TechnicalIndicatorsDataService(TechnicalIndicatorsDataRepository repository,
			TechnicalIndicatorsRedisService redisService, WebClient.Builder webClientBuilder,
			ObjectMapper objectMapper) {
		this.repository = repository;
		this.redisService = redisService;
		this.webClient = webClientBuilder.baseUrl("https://api.twelvedata.com").build();
		this.objectMapper = objectMapper;
	}

	public List<TechnicalIndicatorsData> getTechnicalIndicatorsData() {
		// 1. Check Redis Cache
		List<TechnicalIndicatorsData> cachedData = redisService.getFromCache();
		if (cachedData != null) {
			return cachedData;
		}

		// 2. Check Database
        List<TechnicalIndicatorsData> dbData = repository.findAll();
        if (!dbData.isEmpty()) {
            redisService.saveToCache(dbData);
            return dbData;
        }
		
		
		/*
		 * // 2. Check Database Optional<TechnicalIndicatorsData> dbData =
		 * repository.findById(1L); // Assuming only one record for simplicity if
		 * (dbData.isPresent()) { redisService.saveToCache(dbData.get()); return
		 * dbData.get(); }
		 */

		// 3. Fetch from API
        List<TechnicalIndicatorsData>  fetchedData = fetchFromApi();
		repository.saveAll(fetchedData);
		redisService.saveToCache(fetchedData);
		return fetchedData;
	}

	private List<TechnicalIndicatorsData> fetchFromApi() {

		String url = UriComponentsBuilder.fromUriString(API_URL + "/technical_indicators?").queryParam("source", "docs")
				.build().toString();
		String response = webClient.get().uri(url).retrieve().bodyToMono(String.class).block();

		 List<TechnicalIndicatorsData> dataList = new ArrayList<>();
	        try {
	            JsonNode rootNode = objectMapper.readTree(response).get("data");

	            rootNode.fieldNames().forEachRemaining(key -> {
	                JsonNode node = rootNode.get(key);
	                TechnicalIndicatorsData data = new TechnicalIndicatorsData();
	                data.setName(key);
	                data.setFullName(node.get("full_name").asText());
	                data.setType(node.get("type").asText());
	                data.setOverlay(node.get("overlay").asBoolean());
	                data.setSeriesType(node.path("parameters").path("series_type").path("default").asText(null));
	                data.setDefaultColor(node.path("output_values").path(key).path("default_color").asText());
	                data.setDisplay(node.path("output_values").path(key).path("display").asText());
	                data.setDescription(node.get("description").asText());
	                dataList.add(data);
	            });

	        } catch (Exception e) {
	            throw new RuntimeException("Error fetching data from API", e);
	        }
	        return dataList;
	}
}
