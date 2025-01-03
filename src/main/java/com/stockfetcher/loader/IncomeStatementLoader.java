package com.stockfetcher.loader;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.stockfetcher.model.MetaInfo;
import com.stockfetcher.model.Watchlist;
import com.stockfetcher.model.WatchlistMetaInfo;
import com.stockfetcher.repository.WatchlistRepository;
import com.stockfetcher.service.BalanceSheetService;
import com.stockfetcher.service.IncomeStatementService;

@Component
public class IncomeStatementLoader {

    @Autowired
    private IncomeStatementService incomeStatementService;

    @Autowired
    private BalanceSheetService balanceSheetService;

    @Autowired
    private WatchlistRepository watchlistRepository;

    @Scheduled(cron = "0 0 6 * * MON") // Every Monday at 6 AM
    public void loadIncomeStatements() {
        // Fetch all watchlists
        List<Watchlist> watchlistList = watchlistRepository.findAll();
        if (watchlistList.isEmpty()) {
            System.out.println("No watchlists found for processing.");
            return;
        }

        // Process each watchlist
        for (Watchlist watchlist : watchlistList) {
            if (watchlist.getWatchlistMetaInfos().isEmpty()) {
                System.out.println("No stocks found for watchlist: " + watchlist.getName());
                continue;
            }

            // Process each MetaInfo in the watchlist
            for (WatchlistMetaInfo watchlistMetaInfo : watchlist.getWatchlistMetaInfos()) {
                MetaInfo meta = watchlistMetaInfo.getMetaInfo();
                if (meta == null) {
                    System.out.println("MetaInfo is null for a stock in watchlist: " + watchlist.getName());
                    continue;
                }

                try {
                    // Fetch income statements
                    incomeStatementService.getIncomeStatements(meta.getSymbol(), meta.getExchange());

                    // Fetch balance sheets
                    balanceSheetService.getBalanceSheet(meta.getSymbol(), meta.getExchange(), meta.getMicCode());
                } catch (Exception e) {
                    System.err.println("Error processing MetaInfo with symbol: " + meta.getSymbol());
                    e.printStackTrace();
                }
            }
        }
    }
}
