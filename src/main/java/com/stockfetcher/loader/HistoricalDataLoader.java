package com.stockfetcher.loader;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.stockfetcher.service.HistoricalDataService;

@Component
public class HistoricalDataLoader {

	private final HistoricalDataService historicalDataService;

	public HistoricalDataLoader(HistoricalDataService historicalDataService) {
		this.historicalDataService = historicalDataService;
	}

	/**
	 * Scheduled to run every morning at 6 AM.
	 */
	@Scheduled(cron = "0 0 6 * * ?") // Every day at 6 AM
	public void loadHistoricalData() {
		historicalDataService.fetchHistoricalData("AAPL", "1min");
	}
}
