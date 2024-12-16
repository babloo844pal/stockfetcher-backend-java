package com.stockfetcher.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stockfetcher.model.HistoricalData;
import com.stockfetcher.repository.HistoricalDataRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class HistoricalStockService {
	private final HistoricalDataRepository historicalDataRepository;

	@Autowired
	private AlphaVantageService alphaVantageService;

	@Autowired
	TwelveDataService twelveDataService;

	public void fetchHistoricalData(String symbol) {
		List<HistoricalData> historicalDataList = alphaVantageService.fetchHistoricalData(symbol);
		if (!historicalDataList.isEmpty()) {
			historicalDataRepository.saveAll(historicalDataList);
		}
	}

	public void fetchHistoricalData(String symbol, String interval, String outputSize) {
		List<HistoricalData> historicalDataList = twelveDataService.fetchHistoricalData(symbol, interval, outputSize);
		if (!historicalDataList.isEmpty()) {
			historicalDataRepository.saveAll(historicalDataList);
		}
	}
}
