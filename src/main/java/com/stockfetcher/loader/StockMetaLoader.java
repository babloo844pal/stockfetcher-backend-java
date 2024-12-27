package com.stockfetcher.loader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.stockfetcher.pool.MainLoaderPool;
import com.stockfetcher.service.StockMetaService;

@Component
public class StockMetaLoader extends BaseLoader {

	@Autowired
	private StockMetaService stockMetaService;

	public StockMetaLoader(MainLoaderPool loaderPool) {
		super(loaderPool);
	}

	@Scheduled(fixedRateString = "${loader.stock-price.schedule}")
	@Override
	public void scheduleTask() {
		loaderPool.submitTask(this::loadData);

	}

	@Override
	public void loadData() {
		stockMetaService.getStocksByCountry("India");
		stockMetaService.getStocksByCountry("United States");
	}

}
