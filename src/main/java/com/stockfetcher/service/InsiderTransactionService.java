package com.stockfetcher.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockfetch.cache.InsiderTransactionRedisService;
import com.stockfetcher.model.InsiderTransactionEntity;
import com.stockfetcher.model.MetaInfoEntity;
import com.stockfetcher.repository.InsiderTransactionRepository;

@Service
public class InsiderTransactionService {

    private final InsiderTransactionRepository transactionRepository;
    private final InsiderTransactionRedisService redisService;
    private final MetaInfoService metaInfoService;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

	@Value("${twelve.data.api.key}")
	private String API_KEY;

	@Value("${twelve.data.api.url}")
	private String API_URL;

    public InsiderTransactionService(InsiderTransactionRepository transactionRepository,
                                     InsiderTransactionRedisService redisService,
                                     MetaInfoService metaInfoService,
                                     WebClient.Builder webClientBuilder,
                                     ObjectMapper objectMapper) {
        this.transactionRepository = transactionRepository;
        this.redisService = redisService;
        this.metaInfoService = metaInfoService;
        this.webClient = webClientBuilder.baseUrl("https://api.twelvedata.com").build();
        this.objectMapper = objectMapper;
    }

    /**
     * Get insider transactions by symbol.
     */
    public List<InsiderTransactionEntity> getBySymbol(String symbol) {
        String cacheKey = redisService.getCacheKeyBySymbol(symbol);

        // 1. Check in Redis cache
        List<InsiderTransactionEntity> cachedData = redisService.getFromCache(cacheKey);
        if (!cachedData.isEmpty()) {
            return cachedData;
        }

        // 2. Check in Database
        List<InsiderTransactionEntity> dbData = transactionRepository.findByMetaInfo_Symbol(symbol);
        if (!dbData.isEmpty()) {
            redisService.saveToCache(cacheKey, dbData); // Update Redis cache
            return dbData;
        }

        // 3. Fetch from API and save
        return fetchAndSaveFromApi(symbol);
    }

    /**
     * Get insider transactions by symbol and exchange.
     */
    public List<InsiderTransactionEntity> getBySymbolAndExchange(String symbol, String exchange) {
        String cacheKey = redisService.getCacheKeyBySymbolAndExchange(symbol, exchange);

        // 1. Check in Redis cache
        List<InsiderTransactionEntity> cachedData = redisService.getFromCache(cacheKey);
        if (!cachedData.isEmpty()) {
            return cachedData;
        }

        // 2. Check in Database
        List<InsiderTransactionEntity> dbData = transactionRepository.findByMetaInfo_SymbolAndMetaInfo_Exchange(symbol, exchange);
        if (!dbData.isEmpty()) {
            redisService.saveToCache(cacheKey, dbData); // Update Redis cache
            return dbData;
        }

        // 3. Fetch from API and save
        return fetchAndSaveFromApi(symbol);
    }

    /**
     * Fetch data from the Twelve Data API and save to the database and cache.
     */
    private List<InsiderTransactionEntity> fetchAndSaveFromApi(String symbol) {
		String url = UriComponentsBuilder.fromUriString(API_URL + "/insider_transactions").queryParam("symbol", symbol)
				.queryParam("apikey", API_KEY).queryParam("source", "docs").build().toString();

		String response = webClient.get().uri(url).retrieve().bodyToMono(String.class).block();

        try {
            JsonNode rootNode = objectMapper.readTree(response);

            // Save Meta Info
            JsonNode metaNode = rootNode.path("meta");
            MetaInfoEntity metaInfo = new MetaInfoEntity();
            metaInfo.setSymbol(metaNode.path("symbol").asText());
            metaInfo.setName(metaNode.path("name").asText());
            metaInfo.setCurrency(metaNode.path("currency").asText());
            metaInfo.setExchange(metaNode.path("exchange").asText());
            metaInfo.setMicCode(metaNode.path("mic_code").asText());
            metaInfo.setExchangeTimezone(metaNode.path("exchange_timezone").asText());
            MetaInfoEntity savedMeta = metaInfoService.getOrSaveMetaInfo(metaInfo);

            // Parse and save transactions
            List<InsiderTransactionEntity> transactions = new ArrayList<>();
            for (JsonNode node : rootNode.path("insider_transactions")) {
                InsiderTransactionEntity transaction = new InsiderTransactionEntity();
                transaction.setMetaInfo(savedMeta);
                transaction.setFullName(node.path("full_name").asText());
                transaction.setPosition(node.path("position").asText());
                transaction.setDateReported(LocalDate.parse(node.path("date_reported").asText()));
                transaction.setIsDirect(node.path("is_direct").asBoolean());
                transaction.setShares(node.path("shares").asLong());
                transaction.setValue(BigDecimal.valueOf(node.path("value").asDouble()));
                transaction.setDescription(node.path("description").asText());
                transactions.add(transaction);
            }

            // Save to database
            List<InsiderTransactionEntity> savedTransactions = transactionRepository.saveAll(transactions);

            // Update Redis cache
            redisService.saveToCache(redisService.getCacheKeyBySymbol(symbol), savedTransactions);
            return savedTransactions;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching or saving insider transactions", e);
        }
    }
}
