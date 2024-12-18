package com.stockfetcher.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stockfetcher.model.MetaInfoEntity;

public interface MetaInfoRepository extends JpaRepository<MetaInfoEntity, Long> {
    Optional<MetaInfoEntity> findBySymbol(String symbol);
}
