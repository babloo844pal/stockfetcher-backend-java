package com.stockfetcher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stockfetcher.model.StockProfile;

@Repository
public interface StockProfileRepository extends JpaRepository<StockProfile, Long> {
    StockProfile findBySymbol(String symbol);
}
