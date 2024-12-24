package com.stockfetcher.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stockfetcher.dto.request.WatchlistRequestDto;
import com.stockfetcher.dto.response.WatchlistResponseDto;
import com.stockfetcher.dto.response.WatchlistStockResponseDto;
import com.stockfetcher.model.Watchlist;
import com.stockfetcher.model.WatchlistStock;
import com.stockfetcher.service.WatchlistService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/watchlists")
public class WatchlistController {

	private final WatchlistService watchlistService;

	public WatchlistController(WatchlistService watchlistService) {
		this.watchlistService = watchlistService;
	}

	// Create Watchlist
	@PostMapping(consumes = "application/json", produces = "application/json")
	public WatchlistResponseDto createWatchlist(@Valid @RequestBody WatchlistRequestDto watchlistRequestDto) {
		WatchlistResponseDto watchlistResponseDto = watchlistService.createWatchlist(watchlistRequestDto);
		return watchlistResponseDto;
	}

	// Get Watchlist by ID
	@GetMapping("/{id}")
	public Watchlist getWatchlistById(@PathVariable("id") Long id) {
		return watchlistService.getWatchlistById(id);
	}

	// Get All Watchlist
	@GetMapping(produces = "application/json")
	public List<WatchlistResponseDto> getAllWatchlist() {
		return watchlistService.getAllWatchlist();
	}

	// Get All Watchlists for a User
	@GetMapping(path = "/user/{userId}", produces = "application/json")
	public List<Watchlist> getAllWatchlistsForUser(@PathVariable("userId") Long userId) {
		return watchlistService.getAllWatchlistsForUser(userId);
	}

	// Update Watchlist
	@PutMapping("/{id}")
	public WatchlistResponseDto updateWatchlist(@PathVariable("id") Long id,
			@Valid @RequestBody WatchlistRequestDto watchlistRequestDto) {
		return watchlistService.updateWatchlist(id, watchlistRequestDto);
	}

	// Delete Watchlist
	@DeleteMapping("/{id}")
	public void deleteWatchlist(@PathVariable("id") Long id) {
		watchlistService.deleteWatchlist(id);
	}

	// Add stock to a watchlist
	@PostMapping("/{watchlistId}/stocks")
	public WatchlistStock addStockToWatchlist(@PathVariable("watchlistId") Long watchlistId,
			@RequestParam("stockSymbol") String stockSymbol) {
		return watchlistService.addStockToWatchlist(watchlistId, stockSymbol);
	}

	// Delete stock from a watchlist
	@DeleteMapping("/{watchlistId}/stocks/{stockId}")
	public void deleteStockFromWatchlist(@PathVariable("watchlistId") Long watchlistId,
			@PathVariable("stockId") Long stockId) {
		watchlistService.deleteStockFromWatchlist(watchlistId, stockId);
	}

	// Get stocks in a watchlist
	@GetMapping("/{watchlistId}/stocks")
	public List<WatchlistStockResponseDto> getStocksInWatchlist(@PathVariable("watchlistId") Long watchlistId,
			@RequestParam(value = "sortField", required = false) String sortField,
			@RequestParam(value = "sortOrder", required = false, defaultValue = "asc") String sortOrder,
			@RequestParam(value = "searchQuery", required = false) String searchQuery) {
		return watchlistService.getStocksInWatchlist(watchlistId, sortField, sortOrder, searchQuery);
	}

}
