package com.stockfetcher.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stockfetcher.model.BalanceSheetEntity;

@Repository
public interface BalanceSheetRepository extends JpaRepository<BalanceSheetEntity, Long> {
    List<BalanceSheetEntity> findBySymbol(String symbol);
    List<BalanceSheetEntity> findBySymbolAndExchange(String symbol, String exchange);
}
