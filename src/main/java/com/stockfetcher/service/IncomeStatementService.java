package com.stockfetcher.service;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockfetcher.api.TwelveDataApiClient;
import com.stockfetcher.cache.GenericRedisService;
import com.stockfetcher.constants.CacheConstant;
import com.stockfetcher.model.IncomeStatement;
import com.stockfetcher.model.MetaInfo;
import com.stockfetcher.repository.IncomeStatementRepository;
import com.stockfetcher.repository.MetaInfoRepository;

@Service
public class IncomeStatementService {

	@Autowired
	private IncomeStatementRepository incomeStatementRepository;

	@Autowired
	private MetaInfoRepository metaInfoRepository;

	@Autowired
	private GenericRedisService redisService;

	@Autowired
	private TwelveDataApiClient apiClient;

	public List<IncomeStatement> getIncomeStatements(String symbol, String exchange) {
		String cacheKey = MessageFormat.format(CacheConstant.INCOME_DATA_BYSYMBOL_BYEXCHANGE, symbol, exchange);

		// Fetch from cache
		List<IncomeStatement> cachedData = redisService.get(cacheKey, List.class);
		if (cachedData != null) {
			return cachedData;
		}

		// Fetch MetaInfo
		MetaInfo metaInfo = metaInfoRepository.findBySymbolAndExchange(symbol, exchange)
				.orElseThrow(() -> new RuntimeException("MetaInfo not found for symbol: " + symbol));

		// Fetch from DB
		List<IncomeStatement> dbData = incomeStatementRepository.findByMetaInfo(metaInfo);
		if (!dbData.isEmpty()) {
			redisService.save(cacheKey, dbData, 1440L); // Cache for 1 day
			return dbData;
		}

		// Fetch from API
		List<IncomeStatement> apiData = fetchIncomeStatementFromApi(symbol, exchange);
		if (!apiData.isEmpty()) {
			apiData.forEach(data -> data.setMetaInfo(metaInfo));
			incomeStatementRepository.saveAll(apiData);
			redisService.save(cacheKey, apiData, 1440L); // Cache for 1 day
		}

		return apiData;
	}

	private List<IncomeStatement> fetchIncomeStatementFromApi(String symbol, String exchange) {
		try {

			Map<String, String> queryParams = new HashMap<>();
			queryParams.put("source", "docs");
			queryParams.put("symbol", symbol);
			queryParams.put("exchange", exchange);
			
			String response = apiClient.fetchData("/income_statement", queryParams);

			JsonNode rootNode = new ObjectMapper().readTree(response);
			JsonNode incomeStatementsNode = rootNode.get("income_statement");

			return new ObjectMapper().readValue(incomeStatementsNode.toString(),
					new TypeReference<List<IncomeStatement>>() {
					});
		} catch (Exception e) {
			throw new RuntimeException("Error fetching Income Statement data from API: " + e.getMessage(), e);
		}
	}
}
