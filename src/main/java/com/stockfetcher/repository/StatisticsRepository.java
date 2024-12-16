package com.stockfetcher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stockfetcher.model.StatisticsEntity;

@Repository
public interface StatisticsRepository extends JpaRepository<StatisticsEntity, Long> {
    StatisticsEntity findBySymbol(String symbol);
}
