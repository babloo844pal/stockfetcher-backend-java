package com.stockfetcher.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stockfetcher.model.Indices;
import com.stockfetcher.service.IndicesService;

@RestController
@RequestMapping("/indices")
public class IndicesController {

	private final IndicesService indicesService;

	public IndicesController(IndicesService indicesService) {
		this.indicesService = indicesService;
	}

	/**
	 * Retrieves all indices from the database.
	 *
	 * @return List of indices.
	 */
	@GetMapping("/all")
	public List<Indices> getAllIndices() {
		return indicesService.fetchIndices();
	}

	@GetMapping("/bycountry")
	public List<Indices> getAllIndicesByCountry(@RequestParam("country") String country) {
		return indicesService.getIndicesByCountries(country);
	}

	@GetMapping("/bysymbol")
	public Indices getAllIndicesBySymbol(@RequestParam("symbol") String symbol) {
		return indicesService.getIndicesBySymol(symbol);
	}
}
