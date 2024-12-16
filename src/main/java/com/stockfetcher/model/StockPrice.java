package com.stockfetcher.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "stock_price")
@IdClass(StockPrice.StockPriceId.class)

public class StockPrice implements Serializable{

    private static final long serialVersionUID = 1L;



	@Id
    private String symbol; // Stock symbol (e.g., IBM)

 

    @Column(name = "interval_slice")
    private String intervalSlice; // Interval (e.g., 1h, 1day, 1month)
    
    @Id
    private LocalDateTime datetime; // Datetime for the record

    private BigDecimal open; // Opening price
    private BigDecimal high; // Highest price
    private BigDecimal low; // Lowest price
    private BigDecimal close; // Closing price
    private long volume; // Volume traded

    @Data
    @NoArgsConstructor
    public static class StockPriceId implements Serializable {
        private String symbol;
        private String intervalSlice;
        private LocalDateTime datetime;
    }
}
