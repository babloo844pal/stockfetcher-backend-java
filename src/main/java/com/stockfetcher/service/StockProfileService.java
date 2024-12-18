package com.stockfetcher.service;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockfetcher.cache.ProfileRedisService;
import com.stockfetcher.model.StockProfile;
import com.stockfetcher.repository.StockProfileRepository;

@Service
public class StockProfileService {

    private static final Logger logger = LoggerFactory.getLogger(StockProfileService.class);

    private final StockProfileRepository stockProfileRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    @Autowired
    private ProfileRedisService profileRedisService;
    
	@Value("${twelve.data.api.key}")
	private String API_KEY;

	@Value("${twelve.data.api.url}")
	private String API_URL;

    public StockProfileService(StockProfileRepository stockProfileRepository, RedisTemplate<String, Object> redisTemplate, WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.stockProfileRepository = stockProfileRepository;
        this.redisTemplate = redisTemplate;
        this.webClient = webClientBuilder.baseUrl("https://api.twelvedata.com").build(); // Replace with actual Twelve Data API base URL
        this.objectMapper = objectMapper;
    }

    /**
     * Fetches stock profile details from cache, database, or Twelve Data API.
     *
     * @param symbol The stock symbol.
     * @param apiKey The Twelve Data API key.
     * @return StockProfile object.
     */
    public StockProfile getStockProfile(String symbol, String apiKey) {
        String cacheKey = "stock_profile_" + symbol;

        // Check cache
        StockProfile cachedProfile= profileRedisService.getStockProfile(symbol);
        if (cachedProfile != null) {
            logger.info("Retrieved stock profile from cache for symbol: {}", symbol);
            return cachedProfile;
        }

        // Check database
        StockProfile dbProfile = stockProfileRepository.findBySymbol(symbol);
        if (dbProfile != null) {
            logger.info("Retrieved stock profile from database for symbol: {}", symbol);
            redisTemplate.opsForValue().set(cacheKey, dbProfile, 10, TimeUnit.MINUTES); // Cache the result
            return dbProfile;
        }

        // Fetch from API
        logger.info("Fetching stock profile from API for symbol: {}", symbol);
        String url = UriComponentsBuilder.fromUriString(API_URL + "/profile?").queryParam("symbol", symbol)
				.queryParam("apikey", API_KEY).build().toString();

		String response = webClient.get().uri(url).retrieve().bodyToMono(String.class).block();

        try {
            JsonNode rootNode = objectMapper.readTree(response);
            StockProfile profile = new StockProfile();
            profile.setSymbol(rootNode.get("symbol").asText());
            profile.setName(rootNode.get("name").asText());
            profile.setExchange(rootNode.get("exchange").asText());
            profile.setMicCode(rootNode.get("mic_code").asText());
            profile.setSector(rootNode.get("sector").asText());
            profile.setIndustry(rootNode.get("industry").asText());
            profile.setEmployees(rootNode.get("employees").asInt());
            profile.setWebsite(rootNode.get("website").asText());
            profile.setDescription(rootNode.get("description").asText());
            profile.setType(rootNode.get("type").asText());
            profile.setCeo(rootNode.get("CEO").asText());
            profile.setAddress(rootNode.get("address").asText());
            profile.setAddress2(rootNode.get("address2").asText());
            profile.setCity(rootNode.get("city").asText());
            profile.setZip(rootNode.get("zip").asText());
            profile.setState(rootNode.get("state").asText());
            profile.setCountry(rootNode.get("country").asText());
            profile.setPhone(rootNode.get("phone").asText());

            // Save to DB
            stockProfileRepository.save(profile);

            // Save to cache
            profileRedisService.saveStockProfile(profile);

            return profile;
        } catch (Exception e) {
            throw new RuntimeException("Error parsing API response for stock profile: " + e.getMessage(), e);
        }
    }
}
