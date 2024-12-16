package com.stockfetcher.repository;

import com.stockfetcher.model.Metadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetadataRepository extends JpaRepository<Metadata, String> {
    // Custom query methods can be added here if needed
}
