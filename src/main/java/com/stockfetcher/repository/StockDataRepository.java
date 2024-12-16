package com.stockfetcher.repository;

import com.stockfetcher.model.StockData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockDataRepository extends JpaRepository<StockData, Long> {
    // Custom query methods can be added here if needed
}
