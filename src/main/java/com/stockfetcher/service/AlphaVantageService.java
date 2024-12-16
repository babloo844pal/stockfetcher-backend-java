package com.stockfetcher.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.stockfetcher.exception.ApiException;
import com.stockfetcher.model.HistoricalData;
import com.stockfetcher.model.StockData;

@Service
public class AlphaVantageService {

    private  WebClient webClient;
    
    @Autowired
    private StockService stockService;

    @Value("${alpha.vantage.api.key}")
    private  String API_KEY ;
    
    @Value("${alpha.vantage.api.url}")
    private  String API_URL ;

    public AlphaVantageService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(API_URL).build();
    }

    /**
     * Fetches historical stock data for the given symbol.
     *
     * @param symbol Stock symbol (e.g., IBM, AAPL)
     * @return List of StockData containing historical data
     */
    public List<HistoricalData> fetchHistoricalData(String symbol) {
        String endpoint = "/query?function=TIME_SERIES_DAILY&symbol=" + symbol + "&apikey=" + API_KEY;

        Map<String, Object> response = webClient.get()
                .uri(endpoint)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null || !response.containsKey("Time Series (Daily)")) {
            throw new ApiException("Failed to fetch historical data for symbol: " + symbol);
        }

        Map<String, Map<String, String>> timeSeries = (Map<String, Map<String, String>>) response.get("Time Series (Daily)");

        
        List<HistoricalData> historicalStockDataList = new ArrayList<>();
        for (Map.Entry<String, Map<String, String>> entry : timeSeries.entrySet()) {
            String date = entry.getKey();
            Map<String, String> values = entry.getValue();

            HistoricalData historicalData = new HistoricalData();
            historicalData.setSymbol(symbol);
            historicalData.setTimestamp(LocalDateTime.parse(date));
            historicalData.setOpen(new BigDecimal(values.get("1. open")));
            historicalData.setHigh(new BigDecimal(values.get("2. high")));
            historicalData.setLow(new BigDecimal(values.get("3. low")));
            historicalData.setClose(new BigDecimal(values.get("4. close")));
            historicalData.setVolume(Long.parseLong(values.get("5. volume")));

            historicalStockDataList.add(historicalData);
        }

        return historicalStockDataList;
    }

    /**
     * Fetches real-time stock data for the given symbol.
     *
     * @param symbol Stock symbol (e.g., IBM, AAPL)
     * @return StockData containing the real-time quote
     */
    public StockData fetchRealTimeData(String symbol) {
        String endpoint = "/query?function=GLOBAL_QUOTE&symbol=" + symbol + "&apikey=" + API_KEY;

        Map<String, Object> response = webClient.get()
                .uri(endpoint)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null || !response.containsKey("Global Quote")) {
            throw new ApiException("Failed to fetch real-time data for symbol: " + symbol);
        }

        Map<String, String> quote = (Map<String, String>) response.get("Global Quote");

        StockData stockData = new StockData();
        stockData.setSymbol(symbol);
        stockData.setPrice(new BigDecimal(quote.get("05. price")));
        stockData.setVolume(Long.parseLong(quote.get("06. volume")));
        stockData.setTimestamp(LocalDateTime.parse(quote.get("07. latest trading day")));

        return stockData;
    }
}
