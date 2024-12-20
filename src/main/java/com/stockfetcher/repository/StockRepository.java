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

	StockEntity findByCountryAndExchangeAndSymbol(String country, String exchange, String symbol);

	// Dynamically derived query with optional parameters
	List<StockEntity> findTop10ByCountryIgnoreCaseAndExchangeIgnoreCaseAndSymbolStartingWithIgnoreCase(String country,
			String exchange, String prefix);

	// For cases where some parameters are null, use overloaded methods or nullable
	// fields
	List<StockEntity> findTop10ByExchangeIgnoreCaseAndSymbolStartingWithIgnoreCase(String exchange, String prefix);

	List<StockEntity> findTop10BySymbolStartingWithIgnoreCase(String prefix);

}
