package com.stockfetcher.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stockfetcher.model.Indices;

@Repository
public interface IndicesRepository extends JpaRepository<Indices, Long> {

	List<Indices> findByCountryIgnoreCase(String country);

	Indices findBySymbolIgnoreCase(String symbol);
}
