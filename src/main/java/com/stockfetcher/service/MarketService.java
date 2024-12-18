package com.stockfetcher.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockfetch.cache.MarketRedisService;
import com.stockfetcher.model.MarketEntity;
import com.stockfetcher.repository.MarketRepository;

@Service
public class MarketService {

	private final MarketRepository marketRepository;
	private final MarketRedisService redisService;
	private final WebClient webClient;
	private final ObjectMapper objectMapper;

	@Value("${twelve.data.api.key}")
	private String API_KEY;

	@Value("${twelve.data.api.url}")
	private String API_URL;

	public MarketService(MarketRepository marketRepository, MarketRedisService redisService,
			WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
		this.marketRepository = marketRepository;
		this.redisService = redisService;
		this.webClient = webClientBuilder.baseUrl("https://api.twelvedata.com").build();
		this.objectMapper = objectMapper;
	}

	public List<MarketEntity> getAllMarkets(String apiKey) {
		List<MarketEntity> cachedMarkets = redisService.getAllMarketsFromCache();
		if (cachedMarkets != null)
			return cachedMarkets;

		List<MarketEntity> dbMarkets = marketRepository.findAll();
		if (!dbMarkets.isEmpty()) {
			redisService.saveAllMarketsToCache(dbMarkets);
			return dbMarkets;
		}

		// 3. Fetch from Twelve Data API if no data in cache or database
		List<MarketEntity> fetchedMarket = fetchMarketsFromApi(apiKey, "", "", "");

		if (!fetchedMarket.isEmpty()) {
			marketRepository.saveAll(fetchedMarket); // Save to database
			redisService.saveAllMarketsToCache(fetchedMarket);// Update cache
			return fetchedMarket;
		}
		throw new RuntimeException("No market data found in cache, database, or API.");
	}

	public List<MarketEntity> getMarketsByCountry(String country, String apiKey) {
		// Check Redis Cache
		List<MarketEntity> cachedMarkets = redisService.getMarketsByCountryFromCache(country);
		if (cachedMarkets != null)
			return cachedMarkets;

		// Check Database
		List<MarketEntity> dbMarkets = marketRepository.findByCountry(country);
		if (!dbMarkets.isEmpty()) {
			redisService.saveMarketsByCountryToCache(country, dbMarkets);
			return dbMarkets;
		}

		// Fetch from API
		List<MarketEntity> fetchedMarkets = fetchMarketsFromApi(apiKey, country, "", "");
		List<MarketEntity> filteredMarkets = fetchedMarkets.stream()
				.filter(market -> market.getCountry().equalsIgnoreCase(country)).toList();

		marketRepository.saveAll(filteredMarkets);
		redisService.saveMarketsByCountryToCache(country, filteredMarkets);
		return filteredMarkets;

	}

	public List<MarketEntity> getMarketsByCountryAndName(String country, String name, String apiKey) {
		// Check Redis Cache
		List<MarketEntity> cachedMarkets = redisService.getMarketsByCountryAndNameFromCache(country, name);
		if (cachedMarkets != null)
			return cachedMarkets;

		// Check Database
		List<MarketEntity> dbMarkets = marketRepository.findByCountryAndName(country, name);
		if (!dbMarkets.isEmpty()) {
			redisService.saveMarketsByCountryAndNameToCache(country, name, dbMarkets);
			return dbMarkets;
		}

		// Fetch from API
		List<MarketEntity> fetchedMarkets = fetchMarketsFromApi(apiKey, country, name, "");
		List<MarketEntity> filteredMarkets = fetchedMarkets.stream()
				.filter(market -> market.getCountry().equalsIgnoreCase(country)).toList();

		marketRepository.saveAll(filteredMarkets);
		redisService.saveMarketsByCountryAndNameToCache(country, name, filteredMarkets);
		return filteredMarkets;

	}

	public List<MarketEntity> getMarketsByCountryAndNameAndCode(String country, String name, String code,
			String apiKey) {
		// Check Redis Cache
		List<MarketEntity> cachedMarkets = redisService.getMarketsByCountryAndNameAndCodeFromCache(country, name, code);
		if (cachedMarkets != null)
			return cachedMarkets;

		// Check Database
		List<MarketEntity> dbMarkets = marketRepository.findByCountryAndNameAndCode(country, name, code);
		if (!dbMarkets.isEmpty()) {
			redisService.saveMarketsByCountryAndNameAndCodeToCache(country, name, code, dbMarkets);
			return dbMarkets;
		}

		// Fetch from API
		List<MarketEntity> fetchedMarkets = fetchMarketsFromApi(apiKey, country, name, code);
		List<MarketEntity> filteredMarkets = fetchedMarkets.stream()
				.filter(market -> market.getCountry().equalsIgnoreCase(country)).toList();

		marketRepository.saveAll(filteredMarkets);
		redisService.saveMarketsByCountryAndNameAndCodeToCache(country, name, code, filteredMarkets);
		return filteredMarkets;

	}

	private List<MarketEntity> fetchMarketsFromApi(String apiKey, String country, String name, String code) {
		String url = UriComponentsBuilder.fromUriString(API_URL + "/market_state?").queryParam("apikey", API_KEY)
				.queryParam("source", "docs").queryParam("country", country).queryParam("name", name)
				.queryParam("code", code).build().toString();
		String response = webClient.get().uri(url).retrieve().bodyToMono(String.class).block();

		try {
			JsonNode rootNode = objectMapper.readTree(response);
			return objectMapper.readValue(rootNode.toString(),
					objectMapper.getTypeFactory().constructCollectionType(List.class, MarketEntity.class));
		} catch (Exception e) {
			throw new RuntimeException("Error fetching data from API", e);
		}
	}
}
