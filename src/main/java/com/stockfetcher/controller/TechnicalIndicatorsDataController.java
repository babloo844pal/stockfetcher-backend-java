package com.stockfetcher.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stockfetcher.model.TechnicalIndicatorsData;
import com.stockfetcher.service.TechnicalIndicatorsDataService;

@RestController
@RequestMapping("/technical-indicators")
public class TechnicalIndicatorsDataController {

    private final TechnicalIndicatorsDataService service;

    public TechnicalIndicatorsDataController(TechnicalIndicatorsDataService service) {
        this.service = service;
    }

    @GetMapping
    public List<TechnicalIndicatorsData> getTechnicalIndicatorsData() {
        return service.getTechnicalIndicatorsData();
    }
}
