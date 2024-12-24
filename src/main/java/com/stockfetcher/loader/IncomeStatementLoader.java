package com.stockfetcher.loader;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.stockfetcher.dto.response.WatchlistResponseDto;
import com.stockfetcher.model.MetaInfo;
import com.stockfetcher.service.IncomeStatementService;
import com.stockfetcher.service.MetaInfoService;
import com.stockfetcher.service.WatchlistService;

@Component
public class IncomeStatementLoader {

	@Autowired
	private IncomeStatementService incomeStatementService;

	@Autowired
	private WatchlistService watchlistService;

	@Autowired
	private MetaInfoService metaInfoService;

	@Scheduled(cron = "0 0 6 * * MON") // Every Monday at 6 AM
	public void loadIncomeStatements() {
		// Load all watchlists
		List<WatchlistResponseDto> watchlistDtoList = watchlistService.getAllWatchlist();

		// Process each watchlist
		for (WatchlistResponseDto watchlistResponseDto : watchlistDtoList) {
			// Fetch symbols and exchanges for each watchlist entry
			List<MetaInfo> metaInfos = metaInfoService.getMetaInfosByWatchlistId(watchlistResponseDto.getId());

			// Fetch and load income statements for each MetaInfo
			metaInfos.forEach(meta -> {
				incomeStatementService.getIncomeStatements(meta.getSymbol(), meta.getExchange());
			});
		}
	}
}
