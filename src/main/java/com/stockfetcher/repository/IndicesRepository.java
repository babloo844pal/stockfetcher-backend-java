package com.stockfetcher.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stockfetcher.model.Indices;

@Repository
public interface IndicesRepository extends JpaRepository<Indices, Long> {
	
	 List<Indices> findByCountry(String country);
	 List<Indices> findByCountryAndExchange(String country,String exchange);
	 Indices findByCountryAndExchangeAndName(String country,String exchange,String name);
	 Indices findByCountryAndExchangeAndSymbol(String country,String exchange,String symbol);
}
