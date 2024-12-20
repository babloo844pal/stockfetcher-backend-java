package com.stockfetcher.api;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class TwelveDataApiClient {

	private final WebClient webClient;

	@Value("${twelve.data.api.key}")
	private String apiKey;

	@Value("${twelve.data.api.url}")
	private String baseUrl;

	@Value("${twelve.data.api.timeout}")
	private long timeout;

	public TwelveDataApiClient(WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder.build();
	}

	/**
	 * Fetch data from the Twelve Data API.
	 *
	 * @param endpoint    The API endpoint (e.g., "/time_series").
	 * @param queryParams A map of query parameters to include in the request.
	 * @return The JSON response from the API.
	 */
	public String fetchData(String endpoint, Map<String, String> queryParams) {
		try {
			// Add API key to query parameters
			queryParams.put("apikey", apiKey);
			String url = buildUrl(baseUrl, endpoint, queryParams);

			return webClient.get().uri(url).retrieve().bodyToMono(String.class).block();

		} catch (WebClientResponseException e) {
			// Handle API-specific errors (e.g., 429 Too Many Requests)
			if (e.getRawStatusCode() == 429) {
				throw new RuntimeException("Rate limit exceeded: " + e.getMessage(), e);
			}
			throw new RuntimeException("Error fetching data from Twelve Data API: " + e.getMessage(), e);
		} catch (Exception e) {
			throw new RuntimeException("Unexpected error fetching data from Twelve Data API: " + e.getMessage(), e);
		}

	}

	public String buildUrl(String baseUrl, String endpoint, Map<String, String> queryParams) {
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl+endpoint);
		queryParams.forEach(uriBuilder::queryParam); // Add all query parameters dynamically
		return uriBuilder.build().toString();
	}
}
