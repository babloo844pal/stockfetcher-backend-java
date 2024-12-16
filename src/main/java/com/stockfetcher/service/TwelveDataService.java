package com.stockfetcher.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.stockfetcher.exception.ApiException;
import com.stockfetcher.model.HistoricalData;
import com.stockfetcher.model.StockData;

@Service
public class TwelveDataService {

	private static final Logger logger = LoggerFactory.getLogger(TwelveDataService.class);

	@Value("${twelve.data.api.key}")
	private String API_KEY;

	@Value("${twelve.data.api.url}")
	private String API_URL;

	@Value("${twelve.data.websocket.url}")
	private String WEBSOCKET_URL;

	private final WebClient webClient;
	
	@Autowired
	private StockService stockService;

	private final List<String> intervelInTime = Arrays.asList(
            "1min",
            "5min",
            "15min",
            "30min",
            "45min",
            "1h",
            "2h",
            "4h",
            "8h"
        );

	public TwelveDataService(WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder.baseUrl(API_URL).build();

	}

	/**
	 * Fetch historical data for a given stock symbol.
	 *
	 * @param symbol     Stock symbol (e.g., AAPL, TSLA).
	 * @param interval   Time interval (e.g., "1day", "1min").
	 * @param outputSize Number of data points (e.g., "30", "100").
	 * @return List of StockData objects.
	 */
	public List<HistoricalData> fetchHistoricalData(String symbol, String interval, String outputSize) {
		logger.info("Fetching historical data for symbol: {}, interval: {}, outputSize: {}", symbol, interval,
				outputSize);

		try {

			String url = UriComponentsBuilder.fromUriString(API_URL + "/time_series").queryParam("symbol", symbol)
					.queryParam("interval", interval).queryParam("outputsize", outputSize).queryParam("apikey", API_KEY)
					.build().toString();

			// String url = "/time_series?symbol=" + symbol + "&interval=" + interval +
			// "&outputsize=" + outputSize + "&apikey=" + API_KEY;

			JsonNode response = webClient.get().uri(url).retrieve().bodyToMono(JsonNode.class).block();

			if (response == null || response.isEmpty()) {
				logger.error("Error response from Twelve Data API: {}", response);
				throw new ApiException("Failed to fetch historical data for symbol: " + symbol);
			}

	        System.out.println(response);
	        stockService.processStockPayload(response.toPrettyString());
			
			
			List<HistoricalData> historicalStockDataList = new ArrayList<>();
			JsonNode values = response.get("values");
			JsonNode meta = response.get("meta");

			for (JsonNode node : values) {
				HistoricalData historicalData = new HistoricalData();
				historicalData.setSymbol(symbol);
				LocalDateTime result ;
				if (!intervelInTime.contains(meta.get("interval").asText())) {
					LocalDate dateTime = LocalDate.parse(node.get("datetime").asText(),
							DateTimeFormatter.ofPattern("yyyy-MM-dd"));
					result=LocalDateTime.of(dateTime, LocalTime.of(0, 0));
				}else {
					 result = LocalDateTime.parse(node.get("datetime").asText(),
								DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
				}



				// LocalDate dateTime = LocalDate.parse(node.get("datetime").asText(),
				// DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss" ));
				historicalData.setTimestamp(result);
				historicalData.setOpen(new BigDecimal(node.get("open").asText()));
				historicalData.setHigh(new BigDecimal(node.get("high").asText()));
				historicalData.setLow(new BigDecimal(node.get("low").asText()));
				historicalData.setClose(new BigDecimal(node.get("close").asText()));
				historicalData.setVolume(node.get("volume").asLong());

				historicalStockDataList.add(historicalData);
			}

			logger.info("Successfully fetched {} historical data points for symbol: {}", historicalStockDataList.size(),
					symbol);

			return historicalStockDataList;

		} catch (Exception e) {
			logger.error("Error occurred while fetching historical data: {}", e.getMessage(), e);
			throw new ApiException("Failed to fetch historical data: " + e.getMessage(), e);
		}
	}

	/**
	 * Fetch real-time data for a given stock symbol.
	 *
	 * @param symbol Stock symbol (e.g., AAPL, TSLA).
	 * @return StockData object containing real-time data.
	 */
	public StockData fetchRealTimeData(String symbol) {
		logger.info("Fetching real-time data for symbol: {}", symbol);

		try {

			String url = UriComponentsBuilder.fromUriString(API_URL + "/quote?").queryParam("symbol", symbol)
					.queryParam("apikey", API_KEY).queryParam("source", "docs").build().toString();

			// String url = "/quote?symbol=" + symbol + "&apikey=" + API_KEY;

			JsonNode response = webClient.get().uri(url).retrieve().bodyToMono(JsonNode.class).block();

			if (response == null || response.isEmpty()) {
				logger.error("Error response from Twelve Data API: {}", response);
				throw new ApiException("Failed to fetch real-time data for symbol: " + symbol);
			}

			StockData stockData = new StockData();
			stockData.setSymbol(symbol);
			stockData.setPrice(new BigDecimal(response.get("close").asText()));
			stockData.setVolume(response.get("volume").asLong());
			
			JsonNode meta = response.get("meta");
			LocalDateTime result ;
			if (!intervelInTime.contains(meta.get("interval").asText())) {
				LocalDate dateTime = LocalDate.parse(response.get("datetime").asText(),
						DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				result=LocalDateTime.of(dateTime, LocalTime.of(0, 0));
			}else {
				 result = LocalDateTime.parse(response.get("datetime").asText(),
							DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			}

			// LocalDate dateTime = LocalDate.parse(node.get("datetime").asText(),
			// DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss" ));
			stockData.setTimestamp(result);

			//LocalDate dateTime = LocalDate.parse(response.get("datetime").asText(),
			//		DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			//stockData.setTimestamp(LocalDateTime.of(dateTime, LocalTime.of(0, 0)));

			logger.info("Successfully fetched real-time data for symbol: {}", symbol);
			return stockData;

		} catch (Exception e) {
			logger.error("Error occurred while fetching real-time data: {}", e.getMessage(), e);
			throw new ApiException("Failed to fetch real-time data: " + e.getMessage(), e);
		}
	}
	
	
	public Double fetchRealTimePrice(String symbol) {
		logger.info("Fetching real-time price for symbol: {}", symbol);
		try {
			String url = UriComponentsBuilder.fromUriString(API_URL + "/price?").queryParam("symbol", symbol)
					.queryParam("apikey", API_KEY).queryParam("source", "docs").build().toString();
				JsonNode response = webClient.get().uri(url).retrieve().bodyToMono(JsonNode.class).block();
			if (response == null || response.isEmpty()) {
				logger.error("Error response from Twelve Data API: {}", response);
				throw new ApiException("Failed to fetch real-time data for symbol: " + symbol);
			}
			double price = response.get("price").asDouble();
			logger.info("Successfully fetched real-time price for symbol: {}", symbol);
			return price;
		} catch (Exception e) {
			logger.error("Error occurred while fetching real-time price: {}", e.getMessage(), e);
			throw new ApiException("Failed to fetch real-time price: " + e.getMessage(), e);
		}
	}
	
	
}
