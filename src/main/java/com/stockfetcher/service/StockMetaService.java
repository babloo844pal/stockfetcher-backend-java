package com.stockfetcher.service;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockfetcher.api.TwelveDataApiClient;
import com.stockfetcher.cache.GenericRedisService;
import com.stockfetcher.cache.StockRedisService;
import com.stockfetcher.constants.APIEndpointsConstant;
import com.stockfetcher.constants.CacheConstant;
import com.stockfetcher.model.StockEntity;
import com.stockfetcher.repository.StockRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StockMetaService {

	private final StockRepository stockRepository;
	private final StockRedisService stockRedisService;
	private final ObjectMapper objectMapper;
	private final GenericRedisService genericRedisService;

	@Autowired
	private TwelveDataApiClient twelveDataApiClient;

	public StockMetaService(StockRepository stockRepository, StockRedisService stockRedisService,
			ObjectMapper objectMapper, GenericRedisService genericRedisService) {
		this.stockRepository = stockRepository;
		this.stockRedisService = stockRedisService;
		this.objectMapper = objectMapper;
		this.genericRedisService = genericRedisService;
	}

	// Get all stocks (cache -> database -> API)
	public List<StockEntity> getAllStocks() {
		// 1. Check Redis Cache
		List<StockEntity> cachedStocks = genericRedisService.get(CacheConstant.STOCK_META_ALL, List.class);
		if (cachedStocks != null) {
			log.info("Data successfully fetched from cache.");
			return cachedStocks;
		}

		// 2. Check Database
		List<StockEntity> allStocks = stockRepository.findAll();
		if (!allStocks.isEmpty()) {
			log.info("Data successfully fetched from Database.");
			genericRedisService.save(CacheConstant.STOCK_META_ALL, allStocks, 1440L);
			log.info("Data successfully stored into cache.");
			
			return allStocks;
		}

		// 3. Fetch from Twelve Data API if no data in cache or database
		List<StockEntity> fetchedStocks = fetchStocksFromApi("", "");
		if (!fetchedStocks.isEmpty()) {
			log.info("Data successfully fetched from api.");
			stockRepository.saveAll(fetchedStocks); // Save to database
			log.info("Data successfully stored into database.");
			stockRedisService.saveAllStocksToCache(fetchedStocks); // Update cache
			log.info("Data successfully stored into cache.");
			return fetchedStocks;
		}

		throw new RuntimeException("No stock data found in cache, database, or API.");
	}

	// Fetch stock data from Twelve Data API
	private List<StockEntity> fetchStocksFromApi(String country, String exchange) {
		try {

			Map<String, String> queryParamMap = new HashMap<>();
			queryParamMap.put("source", "docs");
			queryParamMap.put("country", country);
			queryParamMap.put("exchange", exchange);
			String response = twelveDataApiClient.fetchData(APIEndpointsConstant.STOCK_META, queryParamMap);

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
	public List<StockEntity> getStocksByCountry(String country) {
		// 1. Check Redis Cache
		List<StockEntity> cachedStocks = genericRedisService.get(CacheConstant.STOCK_META_BYCOUNTRY + country,
				List.class);
		if (cachedStocks != null) {
			log.info("Data successfully fetched from cache.");
			return cachedStocks;
		}

		// Check database
		List<StockEntity> dbStocks = stockRepository.findByCountry(country);
		if (!dbStocks.isEmpty()) {
			log.info("Data successfully fetched from Database.");
			genericRedisService.save(CacheConstant.STOCK_META_BYCOUNTRY + country, dbStocks, 1440L);
			log.info("Data successfully stored into cache.");
			return dbStocks;
		}

		// Fetch from API if not in database
		List<StockEntity> fetchedStocks = fetchStocksFromApi(country, "");
		List<StockEntity> filteredStocks = fetchedStocks.stream()
				.filter(stock -> stock.getCountry().equalsIgnoreCase(country)).toList();

		if (!filteredStocks.isEmpty()) {
			log.info("Data successfully fetched from api.");
			stockRepository.saveAll(filteredStocks);
			log.info("Data successfully stored into database.");
			genericRedisService.save(CacheConstant.STOCK_META_BYCOUNTRY + country, filteredStocks, 1440L);
			log.info("Data successfully stored into cache.");
			return filteredStocks;
		}

		throw new RuntimeException("No stocks found for country: " + country);
	}

	// Get stocks by country and exchange
	public List<StockEntity> getStocksByCountryAndExchange(String country, String exchange) {
		// 1. Check Redis Cache

		String cacheKey = MessageFormat.format(CacheConstant.STOCK_META_BYCOUNTRY_BYEXCHANGE, country, exchange);
		List<StockEntity> cachedStocks = genericRedisService.get(cacheKey, List.class);
		if (cachedStocks != null) {
			log.info("Data successfully fetched from cache.");
			return cachedStocks;
		}

		// Check database
		List<StockEntity> dbStocks = stockRepository.findByCountryAndExchange(country, exchange);
		if (!dbStocks.isEmpty()) {
			log.info("Data successfully fetched from Database.");
			genericRedisService.save(cacheKey, dbStocks, 1440L);
			log.info("Data successfully stored into cache.");
			return dbStocks;
		}
		// Fetch from API if not in database
		List<StockEntity> fetchedStocks = fetchStocksFromApi(country, exchange);
		List<StockEntity> filteredStocks = fetchedStocks.stream().filter(
				stock -> stock.getCountry().equalsIgnoreCase(country) && stock.getExchange().equalsIgnoreCase(exchange))
				.toList();

		if (!filteredStocks.isEmpty()) {
			log.info("Data successfully fetched from api.");
			stockRepository.saveAll(filteredStocks);
			log.info("Data successfully stored into database.");
			genericRedisService.save(cacheKey, filteredStocks, 1440L);
			log.info("Data successfully stored into cache.");
			return filteredStocks;
		}

		throw new RuntimeException("No stocks found for country: " + country + " and exchange: " + exchange);
	}

	public List<StockEntity> searchStocksWithCache(String country, String exchange,String prefix) {
		String cacheKey = MessageFormat.format(CacheConstant.STOCK_META_BYCOUNTRY_BYEXCHANGE_BYPREFIX, country, exchange);
		// Check cache first
		List<StockEntity> cachedResults = genericRedisService.getSearchResults(cacheKey,prefix,StockEntity[].class);
		if (cachedResults != null) {
			log.info("Data successfully fetched from cache.");
			return cachedResults;
		}

		// Query database if not in cache
		List<StockEntity> dbResults = stockRepository.findTop10ByCountryIgnoreCaseAndExchangeIgnoreCaseAndSymbolStartingWithIgnoreCase(country,exchange,prefix);
		if (dbResults != null) {
			log.info("Data successfully fetched from Database.");
			// Cache the results for future use
			stockRedisService.saveToCache(cacheKey,prefix, dbResults);
			log.info("Data successfully stored into cache.");
			return dbResults;
		}
		return dbResults;
	}

}
