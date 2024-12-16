package com.stockfetcher.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stockfetcher.model.IncomeStatementEntity;

@Repository
public interface IncomeStatementRepository extends JpaRepository<IncomeStatementEntity, Long> {

    List<IncomeStatementEntity> findBySymbol(String symbol);

    IncomeStatementEntity findBySymbolAndFiscalDate(String symbol, String fiscalDate);
}
