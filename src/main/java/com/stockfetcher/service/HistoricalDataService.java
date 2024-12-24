package com.stockfetcher.service;

import java.text.MessageFormat;
import java.util.ArrayList;
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
import com.stockfetcher.model.HistoricalData;
import com.stockfetcher.model.MetaInfo;
import com.stockfetcher.repository.HistoricalDataRepository;
import com.stockfetcher.repository.MetaInfoRepository;

import jakarta.transaction.Transactional;

@Service
public class HistoricalDataService {

	@Autowired
	private HistoricalDataRepository historicalDataRepository;

	@Autowired
	private MetaInfoRepository metaInfoRepository;

	@Autowired
	private GenericRedisService redisService;

	@Autowired
	private TwelveDataApiClient apiClient;

	@Autowired
	private ObjectMapper objectMapper;

	public List<HistoricalData> fetchHistoricalData(String symbol, String interval) {
		String cacheKey = MessageFormat.format(CacheConstant.HISTORICAL_DATA_BYSYMBOL_BYINTERVAL, symbol, interval);

		// Check cache
		List<HistoricalData> cachedData = redisService.get(cacheKey, List.class);
		if (cachedData != null) {
			return cachedData;
		}

		// Fetch MetaInfo
		// MetaInfo metaInfo = metaInfoRepository.findBySymbolAndInterval(symbol,
		// interval)
		// .orElseThrow(() -> new RuntimeException("Meta information not found for
		// symbol: " + symbol));

		// Fetch from database
		List<HistoricalData> dbData = historicalDataRepository.findByMetaInfoSymbolAndMetaInfoIntervalTime(symbol,
				interval);
		if (!dbData.isEmpty()) {
			redisService.save(cacheKey, dbData, 1440L); // Cache for 1 day
			return dbData;
		}

		// Fetch from API
		List<HistoricalData> apiData = fetchHistoricalDataFromApi(symbol, interval);
		if (!apiData.isEmpty()) {
			historicalDataRepository.saveAll(apiData);
			redisService.save(cacheKey, apiData, 1440L); // Cache for 1 day
			return apiData;
		}

		throw new RuntimeException("Error fetching historical data from Twelve Data API. ");
	}
	
	
	@Transactional
	private List<HistoricalData> fetchHistoricalDataFromApi(String symbol, String interval) {
	    try {
	        Map<String, String> queryParams = new HashMap<>();
	        queryParams.put("source", "docs");
	        queryParams.put("symbol", symbol);
	        queryParams.put("interval", interval);

	        // Fetch data from API
	        String response = apiClient.fetchData("/time_series", queryParams);

	        // Parse JSON response
	        JsonNode rootNode = objectMapper.readTree(response);
	        JsonNode metaNode = rootNode.get("meta");
	        JsonNode valuesNode = rootNode.get("values");

	        // Validate and map meta info
	        MetaInfo metaInfo;
	        if (metaNode != null) {
	            metaInfo = objectMapper.readValue(metaNode.toString(), MetaInfo.class);
	        } else {
	            throw new RuntimeException("Meta information is missing in the API response");
	        }

	        // Check if MetaInfo exists in the database
	        MetaInfo existingMetaInfo = metaInfoRepository.findBySymbolAndIntervalTime(metaInfo.getSymbol(), metaInfo.getIntervalTime())
	                .orElse(null);

	        if (existingMetaInfo == null) {
	            // Persist MetaInfo to the database if not already present
	            metaInfo = metaInfoRepository.save(metaInfo);
	        } else {
	            metaInfo = existingMetaInfo;
	        }

	        // Validate and map historical data values
	        List<HistoricalData> historicalDataList = new ArrayList<>();
	        if (valuesNode != null && valuesNode.isArray()) {
	            for (JsonNode value : valuesNode) {
	                HistoricalData historicalData = objectMapper.readValue(value.toString(), HistoricalData.class);
	                historicalData.setMetaInfo(metaInfo); // Set persisted MetaInfo in each HistoricalData
	                historicalDataList.add(historicalData);
	            }
	        } else {
	            throw new RuntimeException("Values are missing or invalid in the API response");
	        }

	        // Save HistoricalData list to the database
	        return historicalDataList;

	    } catch (Exception e) {
	        throw new RuntimeException("Error fetching historical data from Twelve Data API: " + e.getMessage(), e);
	    }
	}
}
