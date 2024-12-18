package com.stockfetcher.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stockfetch.cache.MetaInfoRedisService;
import com.stockfetcher.model.MetaInfoEntity;
import com.stockfetcher.repository.MetaInfoRepository;

@Service
public class MetaInfoService {

    @Autowired
    private MetaInfoRepository metaInfoRepository;

    @Autowired
    private MetaInfoRedisService redisService;

    public MetaInfoEntity getOrSaveMetaInfo(MetaInfoEntity metaInfo) {
        String cacheKey = redisService.getCacheKey(metaInfo.getSymbol());
        MetaInfoEntity cachedMeta = redisService.getFromCache(cacheKey);

        if (cachedMeta != null) {
            return cachedMeta;
        }

        Optional<MetaInfoEntity> existingMeta = metaInfoRepository.findBySymbol(metaInfo.getSymbol());
        if (existingMeta.isPresent()) {
            redisService.saveToCache(cacheKey, existingMeta.get());
            return existingMeta.get();
        }

        MetaInfoEntity savedMeta = metaInfoRepository.save(metaInfo);
        redisService.saveToCache(cacheKey, savedMeta);
        return savedMeta;
    }
}
