package com.stockfetcher.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

	@JsonProperty("name")
	@Column(name = "name", nullable = true)
	private String name;

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

	@ManyToOne
	@JoinColumn(name = "country_id", nullable = true)
	private Country country;

	@OneToMany(mappedBy = "metaInfo", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<BalanceSheet> balanceSheets;

	@ManyToMany
	@JoinTable(name = "meta_info_countries", joinColumns = @JoinColumn(name = "meta_info_id"), inverseJoinColumns = @JoinColumn(name = "country_id"))
	private Set<Country> countries;

	@OneToMany(mappedBy = "metaInfo", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonBackReference
	private Set<WatchlistMetaInfo> watchlistMetaInfos = new HashSet<>();

	// Helper methods
	public void addWatchlist(Watchlist watchlist) {
		WatchlistMetaInfo watchlistMetaInfo = new WatchlistMetaInfo(watchlist, this);
		watchlistMetaInfos.add(watchlistMetaInfo);
		watchlist.getWatchlistMetaInfos().add(watchlistMetaInfo);
	}

	public void removeWatchlist(Watchlist watchlist) {
		watchlistMetaInfos.removeIf(watchlistMetaInfo -> watchlistMetaInfo.getWatchlist().equals(watchlist));
		watchlist.getWatchlistMetaInfos().removeIf(watchlistMetaInfo -> watchlistMetaInfo.getMetaInfo().equals(this));
	}

}
