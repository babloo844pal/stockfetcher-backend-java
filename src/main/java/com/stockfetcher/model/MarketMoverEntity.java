package com.stockfetcher.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "market_mover")
public class MarketMoverEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;
    private String name;
    private String exchange;

    @Column(name = "mic_code")
    private String micCode;

    private LocalDateTime datetime;

    @Column(name = "last_price")
    private Double lastPrice;

    @Column(name = "high_price")
    private Double highPrice;

    @Column(name = "low_price")
    private Double lowPrice;

    private Long volume;

    private Double change;

    @Column(name = "percent_change")
    private Double percentChange;
}
