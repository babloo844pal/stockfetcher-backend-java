package com.stockfetcher.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockfetcher.cache.StockRedisService;
import com.stockfetcher.model.StockEntity;
import com.stockfetcher.repository.StockRepository;

@Service
public class AllStockService {

	private final StockRepository stockRepository;
	private final StockRedisService stockRedisService;
	private final WebClient webClient;
	private final ObjectMapper objectMapper;

	@Value("${twelve.data.api.key}")
	private String API_KEY;

	@Value("${twelve.data.api.url}")
	private String API_URL;

	public AllStockService(StockRepository stockRepository, StockRedisService stockRedisService,
			WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
		this.stockRepository = stockRepository;
		this.stockRedisService = stockRedisService;
		this.webClient = webClientBuilder.baseUrl("https://api.twelvedata.com").build();
		this.objectMapper = objectMapper;
	}

	// Get all stocks (cache -> database -> API)
	public List<StockEntity> getAllStocks(String apiKey) {
		// 1. Check Redis Cache
		List<StockEntity> cachedStocks = stockRedisService.getAllStocksFromCache();
		if (cachedStocks != null) {
			return cachedStocks;
		}

		// 2. Check Database
		List<StockEntity> allStocks = stockRepository.findAll();
		if (!allStocks.isEmpty()) {
			stockRedisService.saveAllStocksToCache(allStocks);
			return allStocks;
		}

		// 3. Fetch from Twelve Data API if no data in cache or database
		List<StockEntity> fetchedStocks = fetchStocksFromApi(apiKey,"","");

		if (!fetchedStocks.isEmpty()) {
            stockRepository.saveAll(fetchedStocks); // Save to database
            stockRedisService.saveAllStocksToCache(fetchedStocks); // Update cache
            return fetchedStocks;
        }

		throw new RuntimeException("No stock data found in cache, database, or API.");
	}

	// Fetch stock data from Twelve Data API
	private List<StockEntity> fetchStocksFromApi(String apiKey, String country, String exchange) {
		try {

			String url = UriComponentsBuilder.fromUriString(API_URL + "/stocks?").queryParam("apikey", API_KEY)
					.queryParam("source", "docs").queryParam("country", country).queryParam("exchange", exchange)
					.build().toString();
			String response = webClient.get().uri(url).retrieve().bodyToMono(String.class).block();

			JsonNode rootNode = objectMapper.readTree(response);
			JsonNode dataNode = rootNode.get("data");

			if (dataNode != null && dataNode.isArray()) {
                return objectMapper.readValue(dataNode.toString(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, StockEntity.class));
            }
			
		} catch (Exception e) {
			throw new RuntimeException("Error fetching stocks from Twelve Data API: " + e.getMessage(), e);
		}
		return List.of();
	}


	// Get stocks by country
	public List<StockEntity> getStocksByCountry(String country, String apiKey) {
		// Check database
        List<StockEntity> dbStocks = stockRepository.findByCountry(country);
        if (!dbStocks.isEmpty()) {
            return dbStocks;
        }

		// Fetch from API if not in database
		List<StockEntity> fetchedStocks = fetchStocksFromApi(apiKey,country,"");
		List<StockEntity> filteredStocks = fetchedStocks.stream()
                .filter(stock -> stock.getCountry().equalsIgnoreCase(country))
                .toList();

        if (!filteredStocks.isEmpty()) {
            stockRepository.saveAll(filteredStocks);
            return filteredStocks;
        }

		throw new RuntimeException("No stocks found for country: " + country);
	}

	// Get stocks by country and exchange
	public List<StockEntity> getStocksByCountryAndExchange(String country, String exchange, String apiKey) {
		// Check database
        List<StockEntity> dbStocks = stockRepository.findByCountryAndExchange(country, exchange);
        if (!dbStocks.isEmpty()) {
            return dbStocks;
        }
		// Fetch from API if not in database
		List<StockEntity> fetchedStocks = fetchStocksFromApi(apiKey,country,exchange);
		List<StockEntity> filteredStocks = fetchedStocks.stream()
                .filter(stock -> stock.getCountry().equalsIgnoreCase(country) && stock.getExchange().equalsIgnoreCase(exchange))
                .toList();

        if (!filteredStocks.isEmpty()) {
            stockRepository.saveAll(filteredStocks);
            return filteredStocks;
        }

		throw new RuntimeException("No stocks found for country: " + country + " and exchange: " + exchange);
	}
}
