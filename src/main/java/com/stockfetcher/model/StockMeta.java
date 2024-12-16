package com.stockfetcher.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "stock_meta")
public class StockMeta implements Serializable{

    private static final long serialVersionUID = 1L;


	@Id
    private String symbol; // Stock symbol (e.g., IBM)

    
    @Column(name = "interval_slice")
    private String intervalSlice; // Interval (e.g., 1h, 1day, 1month)
    private String currency; // Currency (e.g., USD)
    private String exchange_timezone; // Timezone (e.g., America/New_York)
    private String exchange; // Exchange name (e.g., NYSE)
    private String mic_code; // Market Identifier Code (e.g., XNYS)
    private String type; // Type of stock (e.g., Common Stock)
}
