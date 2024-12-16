package com.stockfetcher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stockfetcher.model.MetaEntity;

@Repository
public interface MetaRepository extends JpaRepository<MetaEntity, Long> {
    MetaEntity findBySymbol(String symbol);
}
