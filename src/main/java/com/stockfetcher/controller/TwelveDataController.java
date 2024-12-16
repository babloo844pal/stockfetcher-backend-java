package com.stockfetcher.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stockfetcher.model.StockData;
import com.stockfetcher.service.HistoricalStockService;
import com.stockfetcher.service.RealTimeStockService;
import com.stockfetcher.service.TwelveDataService;

@RestController
@RequestMapping("/fetchStock/twelvedata")
public class TwelveDataController {

	
	@Autowired
	private HistoricalStockService historicalStockService;

	@Autowired

	private RealTimeStockService realTimeStockService;
	
	@Autowired
	private TwelveDataService twelveDataService;

	@GetMapping("/historical")
	public void getHistoricalData(@RequestParam("symbol") String symbol, @RequestParam("interval") String interval,
			@RequestParam("outputSize") String outputSize) {
		historicalStockService.fetchHistoricalData(symbol, interval, outputSize);
	}

	@GetMapping("/realtime")
	public void getRealTimeData(@RequestParam("symbol") String symbol) {
		realTimeStockService.fetchRealTimeData(symbol);
	}
	
	@GetMapping("/realtime/price")
	public Double getRealTimePrice(@RequestParam("symbol") String symbol) {
		return twelveDataService.fetchRealTimePrice(symbol);
	}
	
}
