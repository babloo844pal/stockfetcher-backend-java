package com.stockfetcher.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stockfetcher.model.WatchlistMetaInfo;

@Repository
public interface WatchlistMetaInfoRepository extends JpaRepository<WatchlistMetaInfo, Long> {
	List<WatchlistMetaInfo> findByWatchlistId(Long watchlistId);

	List<WatchlistMetaInfo> findByMetaInfoId(Long metaInfoId);

	Optional<WatchlistMetaInfo> findByWatchlistIdAndMetaInfoId(Long watchlistId, Long metaInfoId);
}