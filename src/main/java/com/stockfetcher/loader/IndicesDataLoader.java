package com.stockfetcher.loader;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.stockfetcher.service.IndicesService;

@Component
public class IndicesDataLoader {

	private final IndicesService indicesService;

	public IndicesDataLoader(IndicesService indicesService) {
		this.indicesService = indicesService;
	}

	/**
	 * Scheduled to run once a week to refresh index data.
	 */
	@Scheduled(cron = "0 0 0 ? * MON") // Every Monday at midnight
	public void loadIndexData() {
		indicesService.fetchIndices(); // Triggers fetching and storing of index data
	}
}
