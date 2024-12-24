package com.stockfetcher.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "meta_info")
public class MetaInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String symbol;

	@JsonProperty("interval")
	@Column(name = "interval_time", nullable = false)
	private String intervalTime;

	private String currency;

	@JsonProperty("exchange_timezone")
	@Column(name = "exchange_timezone", nullable = false)
	private String exchangeTimezone;

	private String exchange;

	@JsonProperty("mic_code")
	@Column(name = "mic_code", nullable = false)
	private String micCode;

	private String type;

	private String period;

	// Add relation with WatchlistStocks
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "watchlistId", referencedColumnName = "id", nullable = true)
	private Watchlist watchlistId;

}
