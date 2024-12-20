package com.stockfetcher.dto.response;

import java.time.LocalDateTime;

import com.stockfetcher.processor.Watchlist;

import lombok.Data;

@Data
public class WatchlistStockResponseDto {

	private Long id;

	private Watchlist watchlist;

	private String stockSymbol;

	private LocalDateTime addedAt;

}
