package com.stockfetcher.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "watchlist_meta_info", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "watchlist_id", "meta_info_id" }) })
public class WatchlistMetaInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "watchlist_id", nullable = false)
	@JsonBackReference
	private Watchlist watchlist;

	@ManyToOne
	@JoinColumn(name = "meta_info_id", nullable = false)
	@JsonBackReference
	private MetaInfo metaInfo;

	public WatchlistMetaInfo(Watchlist watchlist, MetaInfo metaInfo) {
		this.watchlist = watchlist;
		this.metaInfo = metaInfo;
	}

	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;
	    WatchlistMetaInfo that = (WatchlistMetaInfo) o;
	    return Objects.equals(id, that.id); // Compare by ID only
	}

	@Override
	public int hashCode() {
	    return Objects.hash(id); // Hash by ID only
	}

}
