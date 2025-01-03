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

	/**
	 * Get MetaInfo from cache or database, or save it if not exists.
	 *
	 * @param metaInfo MetaInfo to fetch or save
	 * @return Fetched or saved MetaInfo
	 */
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

	/**
	 * Get MetaInfos by Watchlist ID.
	 *
	 * @param watchlistId ID of the Watchlist
	 * @return List of MetaInfo associated with the Watchlist
	 */
	public List<MetaInfo> getMetaInfosByWatchlistId(Long watchlistId) {
		return metaInfoRepository.findMetaInfosByWatchlistId(watchlistId);
	}

	/**
	 * Get MetaInfo by Symbol and Exchange.
	 *
	 * @param symbol   MetaInfo symbol
	 * @param exchange MetaInfo exchange
	 * @return Fetched MetaInfo
	 */
	public MetaInfo getMetaInfoBySymbolAndExchange(String symbol, String exchange) {
		return metaInfoRepository.findBySymbolAndExchange(symbol, exchange);	}
}
