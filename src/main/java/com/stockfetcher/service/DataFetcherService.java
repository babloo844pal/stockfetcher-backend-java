/*
 * package com.stockfetcher.service;
 * 
 * import java.time.Duration;
 * 
 * import org.springframework.kafka.core.KafkaTemplate; import
 * org.springframework.stereotype.Service; import
 * org.springframework.web.reactive.function.client.WebClient;
 * 
 * import lombok.AllArgsConstructor;
 * 
 * @Service
 * 
 * @AllArgsConstructor public class DataFetcherService {
 * 
 * private final WebClient webClient; private final KafkaTemplate<String,
 * String> kafkaTemplate;
 * 
 * 
 * public void fetchHistoricalData(String symbol) { String response =
 * webClient.get() .uri(uriBuilder ->
 * uriBuilder.path("/historical/{symbol}").build(symbol)) .retrieve()
 * .bodyToMono(String.class) .block(Duration.ofSeconds(10)); // Timeout after 10
 * seconds
 * 
 * kafkaTemplate.send("raw-historical-data", symbol, response); }
 * 
 * public void streamRealTimeData(String symbol) { webClient.get()
 * .uri(uriBuilder -> uriBuilder.path("/realtime/{symbol}").build(symbol))
 * .retrieve() .bodyToFlux(String.class) .subscribe(data ->
 * kafkaTemplate.send("raw-realtime-data", symbol, data)); } }
 */