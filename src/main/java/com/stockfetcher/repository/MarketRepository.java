package com.stockfetcher.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stockfetcher.model.MarketEntity;

@Repository
public interface MarketRepository extends JpaRepository<MarketEntity, Long> {
    List<MarketEntity> findByCountry(String country);
    List<MarketEntity> findByCountryAndName(String country, String name);
    List<MarketEntity> findByCountryAndNameAndCode(String country,String name ,String code);
}
