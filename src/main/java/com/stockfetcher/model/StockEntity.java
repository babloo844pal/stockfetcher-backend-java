package com.stockfetcher.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "stocks")
public class StockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("symbol")
    @Column(name = "symbol", nullable = false)
    private String symbol;

    @JsonProperty("name")
    @Column(name = "name", nullable = false)
    private String name;

    @JsonProperty("currency")
    @Column(name = "currency", nullable = false)
    private String currency;

    @JsonProperty("exchange")
    @Column(name = "exchange", nullable = false)
    private String exchange;

    @JsonProperty("mic_code")
    @Column(name = "mic_code", nullable = false)
    private String micCode;

    @JsonProperty("country")
    @Column(name = "country", nullable = false)
    private String country;

    @JsonProperty("type")
    @Column(name = "type", nullable = false)
    private String type;

    @JsonProperty("figi_code")
    @Column(name = "figi_code", nullable = false)
    private String figiCode;
}
