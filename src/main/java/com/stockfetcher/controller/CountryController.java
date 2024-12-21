package com.stockfetcher.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stockfetcher.model.Country;
import com.stockfetcher.service.CountryService;

@RestController
@RequestMapping("/countries")
public class CountryController {

	private final CountryService countryService;

	public CountryController(CountryService countryService) {
		this.countryService = countryService;
	}

	@GetMapping
	public List<Country> getCountries() {
		return countryService.fetchCountries();
	}
	
	@GetMapping("/{iso3}")
	public Country getCountriesByIso3(@PathVariable("iso3") String iso3) {
	    return countryService.fetchCountriesDetailByISO3(iso3);
	}
}
