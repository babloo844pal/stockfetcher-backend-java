package com.stockfetcher.processor;

import com.stockfetcher.model.StockData;
import org.springframework.stereotype.Component;

@Component
public class HistoricalDataProcessor {

    public StockData preprocessHistoricalData(StockData stockData) {
        // Example preprocessing: normalize symbols
        stockData.setSymbol(stockData.getSymbol().toUpperCase());
        return stockData;
    }
}
