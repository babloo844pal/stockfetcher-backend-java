package com.stockfetcher.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stockfetcher.service.HistoricalStockService;
import com.stockfetcher.service.RealTimeStockService;

@RestController
@RequestMapping("/fetchStock")
public class StockDataController {

    private final RealTimeStockService realTimeStockService;
    private final HistoricalStockService historicalStockService;

    public StockDataController(RealTimeStockService realTimeStockService, HistoricalStockService historicalStockService) {
        this.realTimeStockService = realTimeStockService;
        this.historicalStockService = historicalStockService;
    }

    @GetMapping("/realtime-data")
    public String fetchRealTimeData(@RequestParam String symbol) {
        realTimeStockService.fetchRealTimeData(symbol);
        return "Real-time data fetch initiated for symbol: " + symbol;
    }

    @GetMapping("/historical-data")
    public String fetchHistoricalData(@RequestParam String symbol) {
        historicalStockService.fetchHistoricalData(symbol);
        return "Historical data fetch initiated for symbol: " + symbol;
    }
}
