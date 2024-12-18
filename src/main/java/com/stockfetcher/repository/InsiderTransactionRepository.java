package com.stockfetcher.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stockfetcher.model.InsiderTransactionEntity;

public interface InsiderTransactionRepository extends JpaRepository<InsiderTransactionEntity, Long> {
    List<InsiderTransactionEntity> findByMetaInfo_Symbol(String symbol);
    List<InsiderTransactionEntity> findByMetaInfo_SymbolAndMetaInfo_Exchange(String symbol, String exchange);
}
