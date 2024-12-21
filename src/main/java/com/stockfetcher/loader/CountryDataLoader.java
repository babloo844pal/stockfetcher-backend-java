package com.stockfetcher.loader;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.stockfetcher.service.CountryService;

@Component
public class CountryDataLoader {

	private final CountryService countryService;

	public CountryDataLoader(CountryService countryService) {
		this.countryService = countryService;
	}

	/**
	 * Scheduled to run once a week to refresh country data.
	 */
	@Scheduled(cron = "0 0 0 ? * MON") // Every Monday at midnight
	public void loadCountryData() {
		countryService.fetchCountries(); // Triggers fetching and storing of country data
	}
}
