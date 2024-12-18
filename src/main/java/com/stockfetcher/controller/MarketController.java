package com.stockfetcher.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stockfetcher.model.MarketEntity;
import com.stockfetcher.service.MarketService;

@RestController
@RequestMapping("/markets")
public class MarketController {

    private final MarketService marketService;

    public MarketController(MarketService marketService) {
        this.marketService = marketService;
    }

    @GetMapping
    public List<MarketEntity> getAllMarkets(@RequestParam("apiKey") String apiKey) {
        return marketService.getAllMarkets(apiKey);
    }

    @GetMapping("/country")
    public List<MarketEntity> getMarketsByCountry(@RequestParam("country") String country, @RequestParam("apiKey") String apiKey) {
        return marketService.getMarketsByCountry(country, apiKey);
    }

    @GetMapping("/country-exchange")
    public List<MarketEntity> getMarketsByCountryAndName(@RequestParam("country") String country, @RequestParam("name") String name, @RequestParam("apiKey") String apiKey) {
        return marketService.getMarketsByCountryAndName(country, name, apiKey);
    }
    
    @GetMapping("/country-exchange-code")
    public List<MarketEntity> getMarketsByCountryAndNameAndCode(@RequestParam("country") String country, @RequestParam("name") String name, @RequestParam("code") String code, @RequestParam("apiKey") String apiKey) {
        return marketService.getMarketsByCountryAndNameAndCode(country, name,code, apiKey);
    }
}
