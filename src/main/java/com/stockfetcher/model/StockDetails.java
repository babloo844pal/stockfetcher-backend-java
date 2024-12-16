package com.stockfetcher.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "stock_details")
public class StockDetails {

    @Id
    private String symbol; // Stock symbol (e.g., IBM)
    
    private String name; // Company name
    private String exchange; // Exchange name (e.g., NYSE)
    private String mic_code; // Market Identifier Code (e.g., XNYS)
    private String currency; // Currency used (e.g., USD)
    private LocalDate datetime; // Date of the record
    private Long timestamp; // Unix timestamp for the date
    private BigDecimal open; // Opening price
    private BigDecimal high; // Highest price
    private BigDecimal low; // Lowest price
    private BigDecimal close; // Closing price
    private Long volume; // Volume traded
    private BigDecimal previous_close; // Previous close price
    private BigDecimal change; // Price change
    private BigDecimal percent_change; // Percentage change
    private Long average_volume; // Average trading volume
    private Boolean is_market_open; // Is the market currently open?

    @Embedded
    private FiftyTwoWeek fiftyTwoWeek; // Embedded 52-week range data
}
