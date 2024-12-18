package com.stockfetcher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stockfetcher.model.TechnicalIndicatorsData;

@Repository
public interface TechnicalIndicatorsDataRepository extends JpaRepository<TechnicalIndicatorsData, Long> {
}
