package com.stockfetcher.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WatchlistResponseDto {
	private Long id;
	private Long userId;
	private String name;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private List<MetaInfoResponseDto> metaInfos; // List of MetaInfo in the Watchlist
}
