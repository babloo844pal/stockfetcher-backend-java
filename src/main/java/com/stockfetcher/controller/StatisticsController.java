package com.stockfetcher.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stockfetcher.model.StatisticsEntity;
import com.stockfetcher.service.StatisticsService;

@RestController
@RequestMapping("/fetchStock/twelvedata")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/statistics")
    public StatisticsEntity getStatistics(@RequestParam("symbol") String symbol, @RequestParam("apiKey") String apiKey) {
        return statisticsService.getStatistics(symbol, apiKey);
    }
}
