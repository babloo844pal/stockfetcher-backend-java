package com.stockfetcher.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stockfetcher.model.StockEntity;

@Repository
public interface StockRepository extends JpaRepository<StockEntity, Long> {

    // Find all stocks by country
    List<StockEntity> findByCountry(String country);

    // Find stocks by country and exchange
    List<StockEntity> findByCountryAndExchange(String country, String exchange);
}
