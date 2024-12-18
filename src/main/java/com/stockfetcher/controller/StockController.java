package com.stockfetcher.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stockfetcher.model.StockEntity;
import com.stockfetcher.service.AllStockService;

@RestController
@RequestMapping("/stocks")
public class StockController {

    private final AllStockService allStockService;

    public StockController(AllStockService allStockService) {
        this.allStockService = allStockService;
    }

    // Get all stocks
    @GetMapping
    public List<StockEntity> getAllStocks(@RequestParam("apiKey") String apiKey) {
        return allStockService.getAllStocks(apiKey);
    }

    // Get stocks by country
    @GetMapping("/country")
    public List<StockEntity> getStocksByCountry(@RequestParam("country") String country, @RequestParam("apiKey") String apiKey) {
        return allStockService.getStocksByCountry(country, apiKey);
    }

    // Get stocks by country and exchange
    @GetMapping("/country-exchange")
    public List<StockEntity> getStocksByCountryAndExchange(@RequestParam("country") String country, @RequestParam("exchange") String exchange, @RequestParam("apiKey") String apiKey) {
        return allStockService.getStocksByCountryAndExchange(country, exchange, apiKey);
    }
}
