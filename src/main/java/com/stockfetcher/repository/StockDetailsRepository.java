package com.stockfetcher.repository;

import com.stockfetcher.model.StockDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockDetailsRepository extends JpaRepository<StockDetails, String> {
    // Custom query methods can be added here if needed
}
