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
@Table(name = "balance_sheet")
public class BalanceSheetEntity {

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

    private String period;

    @Column(name = "fiscal_date")
    private String fiscalDate;

    @Convert(converter = JsonNodeConverter.class)
    private JsonNode assets;

    @Convert(converter = JsonNodeConverter.class)
    private JsonNode liabilities;

    @Convert(converter = JsonNodeConverter.class)
    @Column(name = "shareholders_equity")
    private JsonNode shareholdersEquity;
}
