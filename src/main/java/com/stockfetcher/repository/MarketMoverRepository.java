package com.stockfetcher.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stockfetcher.model.MarketMoverEntity;

@Repository
public interface MarketMoverRepository extends JpaRepository<MarketMoverEntity, Long> {
    List<MarketMoverEntity> findByExchange(String exchange);
    List<MarketMoverEntity> findByExchangeAndPercentChangeGreaterThan(String exchange, Double percentChange);
    List<MarketMoverEntity> findByExchangeAndLastPriceGreaterThan(String exchange, Double price);
}
