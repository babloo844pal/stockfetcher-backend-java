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
@Table(name = "balance_sheet")
public class BalanceSheet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "meta_info_id", nullable = false)
	private MetaInfo metaInfo;

	private LocalDate fiscalDate;

	private BigDecimal totalAssets;
	private BigDecimal totalLiabilities;
	private BigDecimal totalShareholdersEquity;

	private BigDecimal currentAssets;
	private BigDecimal nonCurrentAssets;

	private BigDecimal currentLiabilities;
	private BigDecimal nonCurrentLiabilities;

}
