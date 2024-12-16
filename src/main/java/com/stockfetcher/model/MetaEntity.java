package com.stockfetcher.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "statistics_meta")
public class MetaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "symbol", nullable = false, unique = true)
    private String symbol;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "exchange", nullable = false)
    private String exchange;

    @Column(name = "mic_code", nullable = false)
    private String micCode;

    @Column(name = "exchange_timezone", nullable = false)
    private String exchangeTimezone;
}
