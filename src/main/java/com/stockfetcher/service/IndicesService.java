package com.stockfetcher.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockfetcher.model.Indices;
import com.stockfetcher.repository.IndicesRepository;

@Service
public class IndicesService {

    private final IndicesRepository indicesRepository;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
	@Value("${twelve.data.api.key}")
	private String API_KEY;

	@Value("${twelve.data.api.url}")
	private String API_URL;

    public IndicesService(IndicesRepository indicesRepository, WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.indicesRepository = indicesRepository;
        this.webClient = webClientBuilder.baseUrl(API_URL).build(); // Replace with Twelve Data API base URL
        this.objectMapper = objectMapper;
    }

    /**
     * Fetches indices data from the Twelve Data API and saves it to the database.
     *
     * @param apiKey The API key for Twelve Data.
     * @return List of saved IndicesEntity.
     */
    public List<Indices> fetchAndSaveIndices(String apiKey) {
        try {
            // Call the Twelve Data API
			String url = UriComponentsBuilder.fromUriString(API_URL + "/indices").queryParam("apikey", API_KEY)
					.build().toString();

			JsonNode rootNode = webClient.get().uri(url).retrieve().bodyToMono(JsonNode.class).block();
   //         JsonNode rootNode = objectMapper.readTree(response);

            // Parse and save indices
            List<Indices> savedEntities = new ArrayList<>();
            if (rootNode.has("data")) {
                for (JsonNode node : rootNode.get("data")) {
                	Indices entity = new Indices();
                    entity.setSymbol(node.get("symbol").asText());
                    entity.setName(node.get("name").asText());
                    entity.setCountry(node.get("country").asText());
                    entity.setCurrency(node.get("currency").asText());
                    entity.setExchange(node.get("exchange").asText());
                    entity.setMicCode(node.get("mic_code").asText());

                    savedEntities.add(indicesRepository.save(entity));
                }
            }
            return savedEntities;

        } catch (Exception e) {
            throw new RuntimeException("Error fetching indices from API: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves all indices from the database.
     *
     * @return List of IndicesEntity.
     */
    public List<Indices> getAllIndices() {
        return indicesRepository.findAll();
    }
    
    public List<Indices> getAllIndicesByCountry(String country) {
        return indicesRepository.findByCountry(country);
    }
    
    public List<Indices> getAllIndicesByCountryAndExchange(String country,String exchange) {
        return indicesRepository.findByCountryAndExchange(country,exchange);
    }
    
    public Indices getIndicesByCountryAndExchangeAndByName(String country,String exchange,String name) {
        return indicesRepository.findByCountryAndExchangeAndName(country,exchange,name);
    }
    
    public Indices getIndicesByCountryAndExchangeAndBySymbol(String country,String exchange,String symbol) {
        return indicesRepository.findByCountryAndExchangeAndSymbol(country,exchange,symbol);
    }
    
}
