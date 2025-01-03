package com.stockfetcher.repository;

import com.stockfetcher.model.HistoricalData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricalDataRepository extends JpaRepository<HistoricalData, Long> {
    List<HistoricalData> findByMetaInfoSymbolAndIntervalTime(String symbol, String interval);
}
