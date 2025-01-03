package com.stockfetcher.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "watchlist")
public class Watchlist {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long userId;

	private String name;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	@OneToMany(mappedBy = "watchlist", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private Set<WatchlistMetaInfo> watchlistMetaInfos = new HashSet<>();

	// Helper methods
	public void addMetaInfo(MetaInfo metaInfo) {
	    if (metaInfo.getWatchlistMetaInfos() == null) {
	        metaInfo.setWatchlistMetaInfos(new HashSet<>());
	    }

	    WatchlistMetaInfo watchlistMetaInfo = new WatchlistMetaInfo(this, metaInfo);

	    // Avoid duplicate entries
	    if (!this.watchlistMetaInfos.contains(watchlistMetaInfo)) {
	        this.watchlistMetaInfos.add(watchlistMetaInfo);
	        metaInfo.getWatchlistMetaInfos().add(watchlistMetaInfo);
	    }
	}

	public void removeMetaInfo(MetaInfo metaInfo) {
		watchlistMetaInfos.removeIf(watchlistMetaInfo -> watchlistMetaInfo.getMetaInfo().equals(metaInfo));
		metaInfo.getWatchlistMetaInfos().removeIf(watchlistMetaInfo -> watchlistMetaInfo.getWatchlist().equals(this));
	}

}
