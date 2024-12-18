package com.stockfetcher.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockfetcher.cache.IncomeStatementRedisService;
import com.stockfetcher.model.IncomeStatementEntity;
import com.stockfetcher.repository.IncomeStatementRepository;

@Service
public class IncomeStatementService {

    private final IncomeStatementRepository incomeStatementRepository;
    private final IncomeStatementRedisService redisService;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
	@Value("${twelve.data.api.key}")
	private String API_KEY;

	@Value("${twelve.data.api.url}")
	private String API_URL;

    public IncomeStatementService(IncomeStatementRepository incomeStatementRepository, IncomeStatementRedisService redisService, WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.incomeStatementRepository = incomeStatementRepository;
        this.redisService = redisService;
        this.webClient = webClientBuilder.baseUrl("https://api.twelvedata.com").build();
        this.objectMapper = objectMapper;
    }

    public List<IncomeStatementEntity> getIncomeStatements(String symbol, String apiKey) {
        // Check database for all statements of a symbol
        List<IncomeStatementEntity> dbStatements = incomeStatementRepository.findBySymbol(symbol);
        if (!dbStatements.isEmpty()) {
            return dbStatements;
        }
        
        // Fetch from API
        String url = UriComponentsBuilder.fromUriString(API_URL + "/income_statement?").queryParam("symbol", symbol)
				.queryParam("apikey", API_KEY).build().toString();

		String response = webClient.get().uri(url).retrieve().bodyToMono(String.class).block();

        try {
            JsonNode rootNode = objectMapper.readTree(response);
            List<IncomeStatementEntity> statements = new ArrayList<>();

            for (JsonNode statementNode : rootNode.get("income_statement")) {
                IncomeStatementEntity entity = new IncomeStatementEntity();
                entity.setSymbol(symbol);
                entity.setFiscalDate(statementNode.get("fiscal_date").asText());
                entity.setData(statementNode.toString());

                // Save to database and cache
                incomeStatementRepository.save(entity);
                redisService.saveIncomeStatementToCache(entity);
                statements.add(entity);
            }

            return statements;
        } catch (Exception e) {
            throw new RuntimeException("Error processing API response for income statements: " + e.getMessage(), e);
        }
    }

    public IncomeStatementEntity getIncomeStatement(String symbol, String fiscalDate, String apiKey) {
        // Check Redis cache
        IncomeStatementEntity cachedStatement = redisService.getIncomeStatementFromCache(symbol, fiscalDate);
        if (cachedStatement != null) {
            return cachedStatement;
        }

        // Check database
        IncomeStatementEntity dbStatement = incomeStatementRepository.findBySymbolAndFiscalDate(symbol, fiscalDate);
        if (dbStatement != null) {
            redisService.saveIncomeStatementToCache(dbStatement); // Cache it
            return dbStatement;
        }

        // Fetch from API if not in cache or database
        return getIncomeStatements(symbol, apiKey).stream()
                .filter(statement -> statement.getFiscalDate().equals(fiscalDate))
                .findFirst()
                .orElse(null);
    }
}
