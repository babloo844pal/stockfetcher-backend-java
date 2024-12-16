package com.stockfetcher.processor;

import com.stockfetcher.model.StockData;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class RealTimeDataProcessor {

    private final KafkaTemplate<String, StockData> kafkaTemplate;

    public RealTimeDataProcessor(KafkaTemplate<String, StockData> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void processRealTimeData(StockData stockData) {
        // Perform validation and preprocessing
        if (stockData.getPrice() != null && stockData.getSymbol() != null) {
            kafkaTemplate.send("realtime-stock-data", stockData.getSymbol(), stockData);
        }
    }
}
