package com.stockfetcher.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stockfetcher.model.StockProfile;
import com.stockfetcher.service.StockProfileService;

@RestController
@RequestMapping("/fetchStock")
public class StockProfileController {

    private final StockProfileService stockProfileService;

    public StockProfileController(StockProfileService stockProfileService) {
        this.stockProfileService = stockProfileService;
    }

    @GetMapping("/stock-profile")
    public StockProfile getStockProfile(@RequestParam("symbol") String symbol, @RequestParam("apiKey") String apiKey) {
        return stockProfileService.getStockProfile(symbol, apiKey);
    }
}
