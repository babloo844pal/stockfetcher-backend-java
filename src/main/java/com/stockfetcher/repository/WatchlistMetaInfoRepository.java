package com.stockfetcher.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stockfetcher.model.WatchlistMetaInfo;

@Repository
public interface WatchlistMetaInfoRepository extends JpaRepository<WatchlistMetaInfo, Long> {
	List<WatchlistMetaInfo> findByWatchlistId(Long watchlistId);
}