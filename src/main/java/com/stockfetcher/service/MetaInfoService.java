
package com.stockfetcher.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stockfetcher.cache.MetaInfoRedisService;
import com.stockfetcher.model.MetaInfo;
import com.stockfetcher.repository.MetaInfoRepository;

@Service
public class MetaInfoService {

	@Autowired
	private MetaInfoRepository metaInfoRepository;

	@Autowired
	private MetaInfoRedisService redisService;

	public MetaInfo getOrSaveMetaInfo(MetaInfo metaInfo) {
		String cacheKey = redisService.getCacheKey(metaInfo.getSymbol());
		MetaInfo cachedMeta = redisService.getFromCache(cacheKey);

		if (cachedMeta != null) {
			return cachedMeta;
		}

		Optional<MetaInfo> existingMeta = metaInfoRepository.findBySymbol(metaInfo.getSymbol());
		if (existingMeta.isPresent()) {
			redisService.saveToCache(cacheKey, existingMeta.get());
			return existingMeta.get();
		}

		MetaInfo savedMeta = metaInfoRepository.save(metaInfo);
		redisService.saveToCache(cacheKey, savedMeta);
		return savedMeta;
	}

	
	public List<MetaInfo> getMetaInfosByWatchlistId(Long watchlistId) {
        // Fetch MetaInfo based on watchlist (logic depends on DB design)
        return metaInfoRepository.findByWatchlistsId(watchlistId);
    }
	
	
	public MetaInfo getMetaInfoBySymbolAndExchange(String symbol,String exchange) {
        // Fetch MetaInfo based on watchlist (logic depends on DB design)
        return metaInfoRepository.findBySymbolAndExchange(symbol,exchange);
    }

}
