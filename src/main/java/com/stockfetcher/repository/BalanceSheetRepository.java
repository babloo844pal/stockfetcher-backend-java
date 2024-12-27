package com.stockfetcher.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stockfetcher.model.BalanceSheet;

@Repository
public interface BalanceSheetRepository extends JpaRepository<BalanceSheet, Long> {
    List<BalanceSheet> findByMetaInfo_SymbolAndMetaInfo_Exchange(String symbol, String exchange);

    List<BalanceSheet> findByMetaInfo_SymbolAndMetaInfo_ExchangeAndMetaInfo_MicCode(
            String symbol, String exchange, String micCode);
}
