package com.stockfetcher.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stockfetcher.model.Watchlist;
import com.stockfetcher.model.WatchlistStock;
import com.stockfetcher.service.WatchlistService;

@RestController
@RequestMapping("/watchlists")
public class WatchlistController {

    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    // Create Watchlist
    @PostMapping
    public Watchlist createWatchlist(@RequestParam("userId") Long userId, @RequestParam("name") String name) {
        return watchlistService.createWatchlist(userId, name);
    }

    // Get Watchlist by ID
    @GetMapping("/{id}")
    public Watchlist getWatchlistById(@PathVariable("id") Long id) {
        return watchlistService.getWatchlistById(id);
    }

    // Get All Watchlists for a User
    @GetMapping("/user/{userId}")
    public List<Watchlist> getAllWatchlistsForUser(@PathVariable("userId") Long userId) {
        return watchlistService.getAllWatchlistsForUser(userId);
    }

    // Update Watchlist
    @PutMapping("/{id}")
    public Watchlist updateWatchlist(@PathVariable("id") Long id, @RequestParam("newName") String newName) {
        return watchlistService.updateWatchlist(id, newName);
    }

    // Delete Watchlist
    @DeleteMapping("/{id}")
    public void deleteWatchlist(@PathVariable("id") Long id) {
        watchlistService.deleteWatchlist(id);
    }

    // Add Stock to Watchlist
    @PostMapping("/{id}/stocks")
    public WatchlistStock addStockToWatchlist(@PathVariable("id") Long id, @RequestParam("stockSymbol") String stockSymbol) {
        return watchlistService.addStockToWatchlist(id, stockSymbol);
    }

    // Get Stocks in Watchlist
    @GetMapping("/{id}/stocks")
    public List<WatchlistStock> getStocksInWatchlist(@PathVariable("id") Long id) {
        return watchlistService.getStocksInWatchlist(id);
    }
}
