package com.stockfetcher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stockfetcher.model.RecommendationsEntity;

@Repository
public interface RecommendationsRepository extends JpaRepository<RecommendationsEntity, Long> {
    RecommendationsEntity findBySymbol(String symbol);
}
