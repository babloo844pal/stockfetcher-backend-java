package com.stockfetcher.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "aggregated_data")
public class AggregatedData {

	@Id
    private String symbol;
    private BigDecimal averagePrice;
    private long totalVolume;

}
