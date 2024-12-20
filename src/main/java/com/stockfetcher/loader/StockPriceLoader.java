package com.stockfetcher.loader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.stockfetcher.pool.MainLoaderPool;

@Component
public class StockPriceLoader extends BaseLoader {

    private final WebClient webClient;

    @Value("${loader.stock-price.schedule}")
    private long scheduleTime;

    public StockPriceLoader(MainLoaderPool loaderPool, WebClient.Builder webClientBuilder) {
        super(loaderPool);
        this.webClient = webClientBuilder.baseUrl("https://api.twelvedata.com").build();
    }

    @Scheduled(fixedRateString = "${loader.stock-price.schedule}")
    @Override
    public void scheduleTask() {
        loaderPool.submitTask(this::loadData);
    }

    @Override
    public void loadData() {
        System.out.println("Fetching stock price data...");

        try {
            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/time_series")
                            .queryParam("symbol", "AAPL,GOOGL,MSFT")
                            .queryParam("interval", "1min")
                            .queryParam("apikey", "demo")
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("Stock Price Data: " + response);
            // Process and store data in Redis/DB
        } catch (Exception e) {
            System.err.println("Error fetching stock price data: " + e.getMessage());
        }
    }
}
