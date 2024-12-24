package com.stockfetcher.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stockfetcher.model.HistoricalData;
import com.stockfetcher.service.HistoricalDataService;

@RestController
@RequestMapping("/historical")
public class HistoricalDataController {

	private final HistoricalDataService historicalDataService;

	public HistoricalDataController(HistoricalDataService historicalDataService) {
		this.historicalDataService = historicalDataService;
	}

	@GetMapping
	public List<HistoricalData> getHistoricalData(@RequestParam("symbol") String symbol, @RequestParam("interval") String interval) {
		return historicalDataService.fetchHistoricalData(symbol, interval);
	}
}
