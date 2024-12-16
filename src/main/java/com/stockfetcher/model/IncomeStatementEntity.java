package com.stockfetcher.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "income_statement")
public class IncomeStatementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "symbol", nullable = false)
    private String symbol;

    @Column(name = "fiscal_date", nullable = false)
    private String fiscalDate; // Storing date as String for consistency with JSON structure

    @Column(name = "data", nullable = false, columnDefinition = "TEXT")
    private String data; // Store the entire income statement JSON for a fiscal date as a single JSON string
}
