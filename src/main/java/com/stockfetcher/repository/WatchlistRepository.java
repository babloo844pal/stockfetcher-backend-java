package com.stockfetcher.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stockfetcher.processor.Watchlist;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
    List<Watchlist> findByUserId(Long userId);
    Optional<Watchlist> findById(Long userId);
   
    // Check if a watchlist with the same name exists
    boolean existsByName(String name);
    
    // Count the number of watchlists for a specific user
    long countByUserId(Long userId);
}
