package com.stockfetcher.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stockfetcher.model.WatchlistStock;

@Repository
public interface WatchlistStockRepository extends JpaRepository<WatchlistStock, Long> {
    
    // Fetch all stocks in a specific watchlist
    List<WatchlistStock> findByWatchlistId(Long watchlistId);

    // Count the number of stocks in a watchlist
    long countByWatchlistId(Long watchlistId);
}
