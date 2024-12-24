package com.stockfetcher.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "income_statement")
public class IncomeStatement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "meta_info_id", nullable = false)
    private MetaInfo metaInfo;

    private LocalDate fiscalDate;
    private Long sales;
    private Long costOfGoods;
    private Long grossProfit;
    private Long researchAndDevelopment;
    private Long sellingGeneralAndAdministrative;
    private Long operatingIncome;
    private Long nonOperatingInterestIncome;
    private Long nonOperatingInterestExpense;
    private Long otherIncomeExpense;
    private Long pretaxIncome;
    private Long incomeTax;
    private Long netIncome;
    private BigDecimal epsBasic;
    private BigDecimal epsDiluted;
    private Long ebit;
    private Long ebitda;
}

