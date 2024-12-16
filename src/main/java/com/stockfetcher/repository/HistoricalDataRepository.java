package com.stockfetcher.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stockfetcher.model.HistoricalData;

public interface HistoricalDataRepository extends JpaRepository<HistoricalData, String> {

}
