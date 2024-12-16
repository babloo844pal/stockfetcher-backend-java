package com.stockfetcher.service;

import com.stockfetcher.model.Metadata;
import com.stockfetcher.repository.MetadataRepository;
import org.springframework.stereotype.Service;

@Service
public class MetadataService {

    private final MetadataRepository metadataRepository;

    public MetadataService(MetadataRepository metadataRepository) {
        this.metadataRepository = metadataRepository;
    }

    public void saveMetadata(Metadata metadata) {
        metadataRepository.save(metadata);
    }
}
