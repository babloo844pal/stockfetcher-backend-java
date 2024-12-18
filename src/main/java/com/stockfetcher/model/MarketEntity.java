package com.stockfetcher.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "market")
public class MarketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("name")
    @Column(name = "name", nullable = false)
    private String name;

    @JsonProperty("code")
    @Column(name = "code", nullable = false)
    private String code;

    @JsonProperty("country")
    @Column(name = "country", nullable = false)
    private String country;

    @JsonProperty("is_market_open")
    @Column(name = "is_market_open", nullable = false)
    private Boolean isMarketOpen;

    @JsonProperty("time_to_open")
    @Column(name = "time_to_open", nullable = false)
    private String timeToOpen;

    @JsonProperty("time_to_close")
    @Column(name = "time_to_close", nullable = false)
    private String timeToClose;

    @JsonProperty("time_after_open")
    @Column(name = "time_after_open", nullable = false)
    private String timeAfterOpen;
}
