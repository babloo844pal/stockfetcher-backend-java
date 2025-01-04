package com.stockfetcher.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.stockfetcher.model.MetaInfo;
import com.stockfetcher.model.Watchlist;
import com.stockfetcher.model.WatchlistMetaInfo;
import com.stockfetcher.repository.MetaInfoRepository;
import com.stockfetcher.repository.WatchlistMetaInfoRepository;
import com.stockfetcher.repository.WatchlistRepository;

@Service
public class WatchlistMetaInfoService {

    @Autowired
    private WatchlistMetaInfoRepository watchlistMetaInfoRepository;

    @Autowired
    private WatchlistRepository watchlistRepository;

    @Autowired
    private MetaInfoRepository metaInfoRepository;

    /**
     * Add a relationship between a Watchlist and MetaInfo.
     */
    public WatchlistMetaInfo saveRelation(Long watchlistId, String symbol) {
        // Fetch Watchlist and MetaInfo
        Watchlist watchlist = watchlistRepository.findById(watchlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist not found with ID: " + watchlistId));
        MetaInfo metaInfo = metaInfoRepository.findBySymbol(symbol)
                .orElseThrow(() -> new ResourceNotFoundException("MetaInfo not found with symbol: " + symbol));

        // Check for existing relation
        if (watchlist.getWatchlistMetaInfos().stream()
                .anyMatch(wmi -> wmi.getMetaInfo().equals(metaInfo))) {
            throw new RuntimeException("MetaInfo already exists in the Watchlist.");
        }

        // Create and save relation
        WatchlistMetaInfo watchlistMetaInfo = new WatchlistMetaInfo(watchlist, metaInfo);
        return watchlistMetaInfoRepository.save(watchlistMetaInfo);
    }

    /**
     * Fetch all MetaInfo for a given Watchlist.
     */
    public List<WatchlistMetaInfo> getRelationsByWatchlistId(Long watchlistId) {
        return watchlistMetaInfoRepository.findByWatchlistId(watchlistId);
    }

    /**
     * Fetch all Watchlists for a given MetaInfo.
     */
    public List<WatchlistMetaInfo> getRelationsByMetaInfoId(Long metaInfoId) {
        return watchlistMetaInfoRepository.findByMetaInfoId(metaInfoId);
    }

    /**
     * Delete a relation between a Watchlist and MetaInfo.
     */
    public void deleteRelation(Long watchlistId, Long metaInfoId) {
        WatchlistMetaInfo relation = watchlistMetaInfoRepository.findByWatchlistIdAndMetaInfoId(watchlistId, metaInfoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Relation not found for Watchlist ID: " + watchlistId + " and MetaInfo ID: " + metaInfoId));
        watchlistMetaInfoRepository.delete(relation);
    }
}
