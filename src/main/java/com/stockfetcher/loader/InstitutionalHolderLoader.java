package com.stockfetcher.loader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.stockfetcher.pool.MainLoaderPool;

@Component
public class InstitutionalHolderLoader extends BaseLoader {

    private final WebClient webClient;

    @Value("${loader.institutional-holders.schedule}")
    private long scheduleTime;

    public InstitutionalHolderLoader(MainLoaderPool loaderPool, WebClient.Builder webClientBuilder) {
        super(loaderPool);
        this.webClient = webClientBuilder.baseUrl("https://api.twelvedata.com").build();
    }

    @Scheduled(fixedRateString = "${loader.institutional-holders.schedule}")
    @Override
    public void scheduleTask() {
        loaderPool.submitTask(this::loadData);
    }

    @Override
    public void loadData() {
        System.out.println("Fetching institutional holder data...");

        try {
            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/institutional_holders")
                            .queryParam("symbol", "AAPL")
                            .queryParam("apikey", "demo")
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("Institutional Holder Data: " + response);
            // Process and store data in Redis/DB
        } catch (Exception e) {
            System.err.println("Error fetching institutional holder data: " + e.getMessage());
        }
    }
}
