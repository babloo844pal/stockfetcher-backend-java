package com.stockfetcher.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stockfetcher.model.MetaInfo;

@Repository
public interface MetaInfoRepository extends JpaRepository<MetaInfo, Long> {
	Optional<MetaInfo> findBySymbolAndIntervalTime(String symbol, String intervalTime);
	Optional<MetaInfo> findBySymbolAndExchange(String symbol, String exchange);
	Optional<MetaInfo> findBySymbol(String symbol);
	
    @Query("SELECT m FROM MetaInfo m WHERE m.watchlistId = :watchlistId")
    List<MetaInfo> findByWatchlistId(@Param("watchlistId") Long watchlistId);
    
}
