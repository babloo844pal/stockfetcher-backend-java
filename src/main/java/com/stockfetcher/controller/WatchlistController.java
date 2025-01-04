package com.stockfetcher.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
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
import com.stockfetcher.dto.response.MetaInfoResponseDto;
import com.stockfetcher.dto.response.WatchlistResponseDto;
import com.stockfetcher.model.Watchlist;
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
	public ResponseEntity<String> addStockToWatchlist(@PathVariable("watchlistId") Long watchlistId,
			@RequestParam("stockSymbol") String stockSymbol) {
		// watchlistService.addMetaInfoToWatchlist(watchlistId, stockSymbol);
		watchlistService.addMetaInfoToWatchlist(watchlistId, stockSymbol);
		return ResponseEntity.ok("MetaInfo added to Watchlist successfully.");
	}

	// Delete stock from a watchlist
	@DeleteMapping("/{watchlistId}/stocks/{metaInfoId}")
	public ResponseEntity<String> deleteStockFromWatchlist(@PathVariable("watchlistId") Long watchlistId,
			@PathVariable("metaInfoId") Long metaInfoId) {
		// watchlistService.removeMetaInfoFromWatchlist(watchlistId, stockSymbol);

        watchlistService.removeMetaInfoFromWatchlist(watchlistId, metaInfoId);
        return ResponseEntity.ok("MetaInfo removed from Watchlist successfully.");
	}

	// Get stocks in a watchlist
	@GetMapping("/{watchlistId}/stocks")
	public List<MetaInfoResponseDto> getStocksInWatchlist(@PathVariable("watchlistId") Long watchlistId,
			@RequestParam(value = "sortField", required = false) String sortField,
			@RequestParam(value = "sortOrder", required = false, defaultValue = "asc") String sortOrder,
			@RequestParam(value = "searchQuery", required = false) String searchQuery) {
		return watchlistService.getMetaInfosInWatchlist(watchlistId, sortField, sortOrder, searchQuery);
	}

}
