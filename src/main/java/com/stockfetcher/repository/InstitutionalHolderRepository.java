package com.stockfetcher.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stockfetcher.model.InstitutionalHolderEntity;

public interface InstitutionalHolderRepository extends JpaRepository<InstitutionalHolderEntity, Long> {
    List<InstitutionalHolderEntity> findByMetaInfo_Symbol(String symbol);
    List<InstitutionalHolderEntity> findByMetaInfo_SymbolAndMetaInfo_Exchange(String symbol, String exchange);
}
