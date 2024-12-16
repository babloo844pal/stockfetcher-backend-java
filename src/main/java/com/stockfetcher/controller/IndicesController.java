package com.stockfetcher.controller;

import com.stockfetcher.model.Indices;
import com.stockfetcher.service.IndicesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/fetchStock/twelvedata")
public class IndicesController {

    private final IndicesService indicesService;

    public IndicesController(IndicesService indicesService) {
        this.indicesService = indicesService;
    }

    /**
     * Fetches indices data from the Twelve Data API and saves it to the database.
     *
     * @param apiKey The API key for Twelve Data.
     * @return List of saved indices.
     */
    @GetMapping("/fetch-indices")
    public List<Indices> fetchAndSaveIndices(@RequestParam("apiKey") String apiKey) {
        return indicesService.fetchAndSaveIndices(apiKey);
    }

    /**
     * Retrieves all indices from the database.
     *
     * @return List of indices.
     */
    @GetMapping("/all")
    public List<Indices> getAllIndices() {
        return indicesService.getAllIndices();
    }
    
    @GetMapping("all/by-country")
    public List<Indices> getAllIndicesByCountry(@RequestParam("country") String country) {
        return indicesService.getAllIndicesByCountry(country);
    }
    
    @GetMapping("all/by-country-exchange")
    public List<Indices> getAllIndicesByCountryAndExchange(@RequestParam("country") String country,@RequestParam("exchange") String exchange) {
        return indicesService.getAllIndicesByCountryAndExchange(country,exchange);
    }
    
    @GetMapping("all/by-country-exchange-name")
    public Indices getAllIndicesByCountryAndExchangeAndName(@RequestParam("country") String country,@RequestParam("exchange") String exchange,@RequestParam("name") String name) {
        return indicesService.getIndicesByCountryAndExchangeAndByName(country,exchange,name);
    }
    
    @GetMapping("all/by-country-exchange-symbol")
    public Indices getAllIndicesByCountryAndExchangeAndSymbol(@RequestParam("country") String country,@RequestParam("exchange") String exchange,@RequestParam("symbol") String symbol) {
        return indicesService.getIndicesByCountryAndExchangeAndBySymbol(country,exchange,symbol);
    }
}
