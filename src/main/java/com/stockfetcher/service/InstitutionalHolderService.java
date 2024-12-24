/*
 * package com.stockfetcher.service;
 * 
 * import java.math.BigDecimal; import java.time.LocalDate; import
 * java.util.ArrayList; import java.util.List;
 * 
 * import org.springframework.beans.factory.annotation.Value; import
 * org.springframework.stereotype.Service; import
 * org.springframework.web.reactive.function.client.WebClient; import
 * org.springframework.web.util.UriComponentsBuilder;
 * 
 * import com.fasterxml.jackson.databind.JsonNode; import
 * com.fasterxml.jackson.databind.ObjectMapper; import
 * com.stockfetcher.cache.InstitutionalHolderRedisService; import
 * com.stockfetcher.model.InstitutionalHolderEntity; import
 * com.stockfetcher.model.MetaInfoEntity; import
 * com.stockfetcher.repository.InstitutionalHolderRepository;
 * 
 * @Service public class InstitutionalHolderService {
 * 
 * private final InstitutionalHolderRepository holderRepository; private final
 * InstitutionalHolderRedisService redisService; private final MetaInfoService
 * metaInfoService; private final WebClient webClient; private final
 * ObjectMapper objectMapper;
 * 
 * @Value("${twelve.data.api.key}") private String API_KEY;
 * 
 * @Value("${twelve.data.api.url}") private String API_URL;
 * 
 * public InstitutionalHolderService(InstitutionalHolderRepository
 * holderRepository, InstitutionalHolderRedisService redisService,
 * MetaInfoService metaInfoService, WebClient.Builder webClientBuilder,
 * ObjectMapper objectMapper) { this.holderRepository = holderRepository;
 * this.redisService = redisService; this.metaInfoService = metaInfoService;
 * this.webClient =
 * webClientBuilder.baseUrl("https://api.twelvedata.com").build();
 * this.objectMapper = objectMapper; }
 * 
 * public List<InstitutionalHolderEntity> getBySymbol(String symbol) { String
 * cacheKey = redisService.getCacheKeyBySymbol(symbol);
 * 
 * // 1. Check in Redis cache List<InstitutionalHolderEntity> cachedData =
 * redisService.getFromCache(cacheKey); if (cachedData != null) return
 * cachedData;
 * 
 * // 2. Check in Database List<InstitutionalHolderEntity> dbData =
 * holderRepository.findByMetaInfo_Symbol(symbol); if (!dbData.isEmpty()) {
 * redisService.saveToCache(cacheKey, dbData); return dbData; }
 * 
 * // 3. Fetch from API return fetchAndSaveFromApi(symbol); }
 * 
 * private List<InstitutionalHolderEntity> fetchAndSaveFromApi(String symbol) {
 * 
 * String url = UriComponentsBuilder.fromUriString(API_URL +
 * "/institutional_holders").queryParam("symbol", symbol) .queryParam("apikey",
 * API_KEY).queryParam("source", "docs").build().toString();
 * 
 * String response =
 * webClient.get().uri(url).retrieve().bodyToMono(String.class).block();
 * 
 * try { JsonNode rootNode = objectMapper.readTree(response); JsonNode metaNode
 * = rootNode.path("meta");
 * 
 * MetaInfoEntity metaInfo = new MetaInfoEntity();
 * metaInfo.setSymbol(metaNode.path("symbol").asText());
 * metaInfo.setName(metaNode.path("name").asText());
 * metaInfo.setCurrency(metaNode.path("currency").asText());
 * metaInfo.setExchange(metaNode.path("exchange").asText());
 * metaInfo.setMicCode(metaNode.path("mic_code").asText());
 * metaInfo.setExchangeTimezone(metaNode.path("exchange_timezone").asText());
 * 
 * MetaInfoEntity savedMeta = metaInfoService.getOrSaveMetaInfo(metaInfo);
 * 
 * List<InstitutionalHolderEntity> holders = new ArrayList<>(); for (JsonNode
 * node : rootNode.path("institutional_holders")) { InstitutionalHolderEntity
 * holder = new InstitutionalHolderEntity(); holder.setMetaInfo(savedMeta);
 * holder.setEntityName(node.path("entity_name").asText());
 * holder.setDateReported(LocalDate.parse(node.path("date_reported").asText()));
 * holder.setShares(node.path("shares").asLong());
 * holder.setValue(BigDecimal.valueOf(node.path("value").asDouble()));
 * holder.setPercentHeld(BigDecimal.valueOf(node.path("percent_held").asDouble()
 * )); holders.add(holder); }
 * 
 * List<InstitutionalHolderEntity> savedHolders =
 * holderRepository.saveAll(holders);
 * redisService.saveToCache(redisService.getCacheKeyBySymbol(symbol),
 * savedHolders); return savedHolders;
 * 
 * } catch (Exception e) { throw new
 * RuntimeException("Error fetching institutional holders from API", e); } } }
 */