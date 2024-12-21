package com.stockfetcher.service;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockfetcher.api.TwelveDataApiClient;
import com.stockfetcher.cache.GenericRedisService;
import com.stockfetcher.constants.CacheConstant;
import com.stockfetcher.model.Country;
import com.stockfetcher.repository.CountryRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CountryService {

	@Autowired
	private  CountryRepository countryRepository;
	
	@Autowired
	private  GenericRedisService redisService;
	
	@Autowired
	private  TwelveDataApiClient apiClient;
	
	@Autowired
	private  ObjectMapper objectMapper;



	public List<Country> fetchCountries() {
		// Check cache
		List<Country> cachedCountries = redisService.get(CacheConstant.COUNTRY_DATA, List.class);
		if (cachedCountries != null) {
			log.info("Data successfully fetched from cache.");
			return cachedCountries;
		}

		// Fetch from database
		List<Country> dbCountries = countryRepository.findAll();
		if (!dbCountries.isEmpty()) {
			log.info("Data successfully fetched from Database.");
			redisService.save(CacheConstant.COUNTRY_DATA, dbCountries, 10080L); // Cache for 1 week
			dbCountries.forEach(country -> {
				redisService.save(MessageFormat.format(CacheConstant.COUNTRY_DATA_BYISO3, country.getName()),
						List.of(country), 10080L); // Cache for 1 week (10080 minutes)
			});
			log.info("Data successfully stored into cache.");
			return dbCountries;
		}

		// Fetch from API
		List<Country> apiCountries = fetchCountriesFromApi();
		if (!apiCountries.isEmpty()) {
			log.info("Data successfully fetched from api.");

			countryRepository.saveAll(apiCountries);
			log.info("Data successfully stored into database.");

			redisService.save(CacheConstant.COUNTRY_DATA, apiCountries, 10080L); // Cache for 1 week
			apiCountries.forEach(country -> {
				redisService.save(MessageFormat.format(CacheConstant.COUNTRY_DATA_BYISO3, country.getName()),
						List.of(country), 10080L); // Cache for 1 week (10080 minutes)
			});
			log.info("Data successfully stored into cache.");
			return apiCountries;
		}
		throw new RuntimeException("No stock data found in cache, database, or API.");
	}

	public Country fetchCountriesDetailByISO3(String iso3) {
		String cacheKey = MessageFormat.format(CacheConstant.COUNTRY_DATA_BYISO3, iso3);
		// Check cache
		Country cachedCountrie = redisService.get(cacheKey, Country.class);
		if (cachedCountrie != null) {
			log.info("Data successfully fetched from cache.");
			return cachedCountrie;
		}

		// Fetch from database
		Country dbCountrie = countryRepository.findByIso3IgnoreCase(iso3);
		if (dbCountrie != null) {
			log.info("Data successfully fetched from Database.");
			redisService.save(cacheKey, dbCountrie, 10080L); // Cache for 1 week
			log.info("Data successfully stored into cache.");
			return dbCountrie;
		}

		fetchCountries();
		throw new RuntimeException("No country data found in cache, database, or API.");
	}

	private List<Country> fetchCountriesFromApi() {
		try {
			Map<String, String> queryParams = new HashMap<>();
			queryParams.put("source", "docs");
			String response = apiClient.fetchData("/countries", queryParams);
			// Parse response to List<Country> (example omitted for brevity)
			JsonNode rootNode = objectMapper.readTree(response);
			JsonNode dataNode = rootNode.get("data");

			if (dataNode != null && dataNode.isArray()) {
				return objectMapper.readValue(dataNode.toString(),
						objectMapper.getTypeFactory().constructCollectionType(List.class, Country.class));
			}

		} catch (Exception e) {
			throw new RuntimeException("Error fetching stocks from Twelve Data API: " + e.getMessage(), e);
		}
		return List.of();

	}
}
