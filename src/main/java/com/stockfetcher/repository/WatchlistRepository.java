package com.stockfetcher.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stockfetcher.model.Watchlist;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
	List<Watchlist> findByUserId(Long userId);

	Optional<Watchlist> findById(Long userId);

	// Check if a watchlist with the same name exists
	boolean existsByName(String name);

	// Count the number of watchlists for a specific user
	long countByUserId(Long userId);

	// Find all Watchlists linked to a specific MetaInfo ID
	//List<Watchlist> findByMetaInfosId(Long metaInfoId);

	// Check if a watchlist with the same name exists for a user
	boolean existsByNameAndUserId(String name, Long userId);

	// Fetch all MetaInfo for a given Watchlist ID
	// List<WatchlistMetaInfo> findByWatchlistId(Long watchlistId);

//	@Query("SELECT w FROM Watchlist w JOIN w.metaInfos m WHERE m.id = :metaInfoId")
//	List<Watchlist> findByMetaInfoId(Long metaInfoId);

	@Query("SELECT w FROM Watchlist w JOIN w.watchlistMetaInfos wm WHERE wm.metaInfo.id = :metaInfoId")
	List<Watchlist> findWatchlistsByMetaInfoId(@Param("metaInfoId") Long metaInfoId);

}
