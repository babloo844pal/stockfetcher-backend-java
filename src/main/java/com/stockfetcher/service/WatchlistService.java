package com.stockfetcher.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.stockfetcher.model.WatchlistStock;
import com.stockfetcher.processor.Watchlist;
import com.stockfetcher.repository.WatchlistRepository;
import com.stockfetcher.repository.WatchlistStockRepository;

@Service
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final WatchlistStockRepository watchlistStockRepository;

    public WatchlistService(WatchlistRepository watchlistRepository, WatchlistStockRepository watchlistStockRepository) {
        this.watchlistRepository = watchlistRepository;
        this.watchlistStockRepository = watchlistStockRepository;
    }

    // Create Watchlist
    public Watchlist createWatchlist(Long userId, String name) {
        Watchlist watchlist = new Watchlist();
        watchlist.setUserId(userId);
        watchlist.setName(name);
        watchlist.setCreatedAt(LocalDateTime.now());
        return watchlistRepository.save(watchlist);
    }

    // Get Watchlist by ID
    public Watchlist getWatchlistById(Long id) {
        return watchlistRepository.findById(id).orElseThrow(() -> new RuntimeException("Watchlist not found"));
    }

    // Get All Watchlists for a User
    public List<Watchlist> getAllWatchlistsForUser(Long userId) {
        return watchlistRepository.findByUserId(userId);
    }

    // Update Watchlist
    public Watchlist updateWatchlist(Long id, String newName) {
        Watchlist watchlist = getWatchlistById(id);
        watchlist.setName(newName);
        watchlist.setUpdatedAt(LocalDateTime.now());
        return watchlistRepository.save(watchlist);
    }

    // Delete Watchlist
    public void deleteWatchlist(Long id) {
        watchlistRepository.deleteById(id);
    }

    // Add Stock to Watchlist
    public WatchlistStock addStockToWatchlist(Long watchlistId, String stockSymbol) {
        WatchlistStock stock = new WatchlistStock();
        stock.setWatchlistId(watchlistId);
        stock.setStockSymbol(stockSymbol);
        stock.setAddedAt(LocalDateTime.now());
        return watchlistStockRepository.save(stock);
    }

    // Get Stocks in a Watchlist
    public List<WatchlistStock> getStocksInWatchlist(Long watchlistId) {
    	List<WatchlistStock> list =watchlistStockRepository.findByWatchlistId(watchlistId);
        return list;
        
    }
}
