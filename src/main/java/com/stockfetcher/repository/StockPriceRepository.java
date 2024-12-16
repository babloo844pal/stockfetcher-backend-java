package com.stockfetcher.repository;

import com.stockfetcher.model.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface StockPriceRepository extends JpaRepository<StockPrice, StockPrice.StockPriceId> {

    /**
     * Checks if a record exists for a given symbol, interval slice, and datetime.
     *
     * @param symbol        The stock symbol (e.g., IBM).
     * @param intervalSlice The interval slice (e.g., 1h, 1day).
     * @param datetime      The datetime for the record.
     * @return True if a record exists, false otherwise.
     */
    boolean existsBySymbolAndIntervalSliceAndDatetime(String symbol, String intervalSlice, LocalDateTime datetime);
}
