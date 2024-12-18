package com.stockfetcher.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stockfetcher.model.RecommendationsEntity;
import com.stockfetcher.service.RecommendationsService;

@RestController
@RequestMapping("/recommendations")
public class RecommendationsController {

    private final RecommendationsService service;

    public RecommendationsController(RecommendationsService service) {
        this.service = service;
    }

    @GetMapping("/{symbol}")
    public RecommendationsEntity getRecommendations(@PathVariable("symbol") String symbol) {
        return service.getRecommendations(symbol);
    }
}
