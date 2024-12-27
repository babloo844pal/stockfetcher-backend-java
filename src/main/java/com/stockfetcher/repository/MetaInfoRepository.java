package com.stockfetcher.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stockfetcher.model.MetaInfo;

@Repository
public interface MetaInfoRepository extends JpaRepository<MetaInfo, Long> {
	Optional<MetaInfo> findBySymbolAndIntervalTime(String symbol, String intervalTime);

	MetaInfo findBySymbolAndExchange(String symbol, String exchange);

	Optional<MetaInfo> findBySymbol(String symbol);

	List<MetaInfo> findByWatchlistsId(Long watchlistId);

}
