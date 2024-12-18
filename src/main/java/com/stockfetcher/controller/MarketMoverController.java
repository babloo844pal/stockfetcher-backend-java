package com.stockfetcher.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stockfetcher.model.MarketMoverEntity;
import com.stockfetcher.service.MarketMoverService;

@RestController
@RequestMapping("/market-mover")
public class MarketMoverController {

    private final MarketMoverService service;

    public MarketMoverController(MarketMoverService service) {
        this.service = service;
    }

    @GetMapping
    public List<MarketMoverEntity> getAllMarketMovers() {
        return service.getAllData();
    }
}
