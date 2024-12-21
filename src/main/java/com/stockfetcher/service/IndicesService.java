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
import com.stockfetcher.constants.CacheConstant;
import com.stockfetcher.model.Country;
import com.stockfetcher.model.Indices;
import com.stockfetcher.repository.IndicesRepository;

@Service
public class IndicesService {

	@Autowired
	private IndicesRepository indicesRepository;

	@Autowired
	private GenericRedisService redisService;

	@Autowired
	private TwelveDataApiClient apiClient;

	@Autowired
	private ObjectMapper objectMapper;

	public List<Indices> fetchIndices() {

		// Check cache
		List<Indices> cachedIndices = redisService.get(CacheConstant.INDICES_DATA, List.class);
		if (cachedIndices != null) {
			return cachedIndices;
		}

		// Fetch from database
		List<Indices> dbIndices = indicesRepository.findAll();
		if (!dbIndices.isEmpty()) {
			redisService.save(CacheConstant.INDICES_DATA, dbIndices, 10080L); // Cache for 1 week
			saveIndicesCacheBySymbol(dbIndices);
			saveIndicesCacheByCountry(dbIndices);
			return dbIndices;
		}

		// Fetch from API
		List<Indices> apiIndices = fetchIndicesFromApi();
		if (!apiIndices.isEmpty()) {
			indicesRepository.saveAll(apiIndices);
			redisService.save(CacheConstant.INDICES_DATA, apiIndices, 10080L);
			saveIndicesCacheBySymbol(apiIndices);
			saveIndicesCacheByCountry(apiIndices);
			return apiIndices;
		}

		
		throw new RuntimeException("No indices data found in cache, database, or API.");
	}

	private List<Indices> fetchIndicesFromApi() {

		try {
			Map<String, String> queryParams = new HashMap<>();
			queryParams.put("source", "docs");
			String response = apiClient.fetchData("/indices", queryParams);
			JsonNode rootNode = objectMapper.readTree(response);
			JsonNode dataNode = rootNode.get("data");

			if (dataNode != null && dataNode.isArray()) {
				return objectMapper.readValue(dataNode.toString(),
						objectMapper.getTypeFactory().constructCollectionType(List.class, Indices.class));
			}

		} catch (Exception e) {
			throw new RuntimeException("Error fetching stocks from Twelve Data API: " + e.getMessage(), e);
		}
		return List.of();

	}

	private void saveIndicesCacheBySymbol(List<Indices> indices) {
		indices.forEach(indice -> {
			redisService.save(MessageFormat.format(CacheConstant.INDICES_DATA_BYSYMBOL, indice.getSymbol()),
					List.of(indice), 10080L); // Cache for 1 week (10080 minutes)
		});
	}

	private void saveIndicesCacheByCountry(List<Indices> indices) {
		indices.forEach(indice -> {
			redisService.save(MessageFormat.format(CacheConstant.INDICES_DATA_BYCOUNTRY, indice.getCountry()),
					List.of(indice), 10080L); // Cache for 1 week (10080 minutes)
		});
	}

	public List<Indices> getIndicesByCountries(String country) {
		String cacheKey = MessageFormat.format(CacheConstant.INDICES_DATA_BYCOUNTRY, country);

		// Check cache
		List<Indices> cachedIndices = redisService.get(cacheKey, List.class);
		if (cachedIndices != null) {
			return cachedIndices;
		}

		// Fetch from database
		List<Indices> dbIndices = indicesRepository.findByCountryIgnoreCase(country);
		if (!dbIndices.isEmpty()) {
			redisService.save(cacheKey, dbIndices, 10080L); // Cache for 1 week
			saveIndicesCacheBySymbol(dbIndices);
			return dbIndices;
		}

		fetchIndices();
		throw new RuntimeException("No country data found in cache, database, or API.");
	}

	public Indices getIndicesBySymol(String symbol) {
		String cacheKey = MessageFormat.format(CacheConstant.INDICES_DATA_BYSYMBOL, symbol);

		// Check cache
		Indices cachedIndice = redisService.get(cacheKey, Indices.class);
		if (cachedIndice != null) {
			return cachedIndice;
		}

		// Fetch from database
		Indices dbIndice = indicesRepository.findBySymbolIgnoreCase(symbol);
		if (dbIndice != null) {
			redisService.save(cacheKey, dbIndice, 10080L); // Cache for 1 week
			return dbIndice;
		}

		fetchIndices();
		throw new RuntimeException("No country data found in cache, database, or API.");
	}
}
