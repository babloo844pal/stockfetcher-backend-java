package com.stockfetcher.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "statistics_data")
public class StatisticsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "symbol", nullable = false)
    private String symbol;

    @Column(name = "data", nullable = false, columnDefinition = "TEXT")
    private String data; // Store the nested JSON (statistics) as a single JSON string
}
