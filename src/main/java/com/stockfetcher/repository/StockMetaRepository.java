package com.stockfetcher.repository;

import com.stockfetcher.model.StockMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockMetaRepository extends JpaRepository<StockMeta, String> {
    // Additional queries if needed
}
