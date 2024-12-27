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
import com.stockfetcher.constants.APIEndpointsConstant;
import com.stockfetcher.constants.CacheConstant;
import com.stockfetcher.model.BalanceSheet;
import com.stockfetcher.model.MetaInfo;
import com.stockfetcher.repository.BalanceSheetRepository;

@Service
public class BalanceSheetService {

	@Autowired
	private BalanceSheetRepository balanceSheetRepository;

	@Autowired
	private MetaInfoService metaInfoService;

	@Autowired
	private GenericRedisService redisCache;

	@Autowired
	private TwelveDataApiClient apiClient;

	public List<BalanceSheet> getBalanceSheet(String symbol, String exchange, String micCode) {
		String cacheKey = MessageFormat.format(CacheConstant.BALANCE_SHEET_BYSYMBOL_BYEXCHANGE_BYMICCODE, symbol,
				exchange, micCode);

		// 1. Check cache
		List<BalanceSheet> cachedData = redisCache.get(cacheKey, List.class);
		if (cachedData != null) {
			return cachedData;
		}

		// 2. Check database
		List<BalanceSheet> dbData = balanceSheetRepository
				.findByMetaInfo_SymbolAndMetaInfo_ExchangeAndMetaInfo_MicCode(symbol, exchange, micCode);
		if (!dbData.isEmpty()) {
			redisCache.save(cacheKey, dbData, 1440L);
			return dbData;
		}

		// 3. Fetch from API
		List<BalanceSheet> balanceSheets = fetchFromApiAndSave(symbol, exchange, micCode);

		if (!balanceSheets.isEmpty()) {
			redisCache.save(cacheKey, balanceSheets, 1440L);
			return balanceSheets;
		}
		throw new RuntimeException("Error fetching balance sheet data from API.");
	}

	private List<BalanceSheet> fetchFromApiAndSave(String symbol, String exchange, String micCode) {

		Map<String, String> queryParams = new HashMap<>();
		queryParams.put("source", "docs");
		queryParams.put("symbol", symbol);
		queryParams.put("exchange", exchange);
		queryParams.put("micCode", micCode);
		String response = apiClient.fetchData(APIEndpointsConstant.BALANCESHEET, queryParams);
		MetaInfo metaInfo = metaInfoService.getMetaInfoBySymbolAndExchange(symbol, exchange);

		return parseApiResponse(response, metaInfo);
	}

	private List<BalanceSheet> parseApiResponse(String response, MetaInfo metaInfo) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(response);
			JsonNode balanceSheetNodes = rootNode.get("balance_sheet");

			List<BalanceSheet> balanceSheets = objectMapper.readerForListOf(BalanceSheet.class)
					.readValue(balanceSheetNodes);

			balanceSheets.forEach(sheet -> sheet.setMetaInfo(metaInfo));
			balanceSheetRepository.saveAll(balanceSheets);
			return balanceSheets;

		} catch (Exception e) {
			throw new RuntimeException("Failed to parse Balance Sheet response", e);
		}
	}
}
