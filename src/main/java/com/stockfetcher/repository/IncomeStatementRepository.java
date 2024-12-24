package com.stockfetcher.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stockfetcher.model.IncomeStatement;
import com.stockfetcher.model.MetaInfo;

@Repository
public interface IncomeStatementRepository extends JpaRepository<IncomeStatement, Long> {
    List<IncomeStatement> findByMetaInfo(MetaInfo metaInfo);
}