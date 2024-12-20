package com.stockfetcher.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stockfetcher.model.StockEntity;
import com.stockfetcher.service.StockMetaService;

@RestController
@RequestMapping("/stocks")
public class StockController {

	@Autowired
	private StockMetaService stockMetaService;

	// Get all stocks
	@GetMapping
	public List<StockEntity> getAllStocks() {
		return stockMetaService.getAllStocks();
	}

	// Get stocks by country
	@GetMapping("/country")
	public List<StockEntity> getStocksByCountry(@RequestParam("country") String country) {
		return stockMetaService.getStocksByCountry(country);
	}

	// Get stocks by country and exchange
	@GetMapping("/country-exchange")
	public List<StockEntity> getStocksByCountryAndExchange(@RequestParam("country") String country,
			@RequestParam("exchange") String exchange) {
		return stockMetaService.getStocksByCountryAndExchange(country, exchange);
	}

	@GetMapping("/search")
	public List<StockEntity> searchStocks(@RequestParam("country") String country,
			@RequestParam("exchange") String exchange, @RequestParam("prefix") String prefix) {
		if (prefix.length() < 3) {
			throw new IllegalArgumentException("Prefix must be at least 3 characters long.");
		}
		return stockMetaService.searchStocksWithCache(country, exchange, prefix);
	}
}
