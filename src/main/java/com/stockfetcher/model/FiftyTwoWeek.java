package com.stockfetcher.model;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Embeddable
public class FiftyTwoWeek {

    private BigDecimal fifty_two_week_low; // 52-week lowest price
    private BigDecimal fifty_two_week_high; // 52-week highest price
    private BigDecimal low_change; // Change from 52-week low
    private BigDecimal high_change; // Change from 52-week high
    private BigDecimal low_change_percent; // Percent change from 52-week low
    private BigDecimal high_change_percent; // Percent change from 52-week high
    private String range; // 52-week range (formatted as "low - high")
}
