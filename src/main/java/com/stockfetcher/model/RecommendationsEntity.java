package com.stockfetcher.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.stockfetcher.converter.JsonNodeConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
@Table(name = "recommendations")
public class RecommendationsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;
    private String name;
    private String currency;
    private String exchange;

    @Column(name = "mic_code")
    private String micCode;

    @Column(name = "exchange_timezone")
    private String exchangeTimezone;

    private String type;

    @Convert(converter = JsonNodeConverter.class)
    @Column(columnDefinition = "TEXT") // JSON stored as text
    private JsonNode trends;

    private Double rating;
    private String status;
}
