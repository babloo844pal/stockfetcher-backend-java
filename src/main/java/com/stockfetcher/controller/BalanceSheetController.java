package com.stockfetcher.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stockfetcher.model.BalanceSheetEntity;
import com.stockfetcher.service.BalanceSheetService;

@RestController
@RequestMapping("/balance-sheet")
public class BalanceSheetController {

    private final BalanceSheetService service;

    public BalanceSheetController(BalanceSheetService service) {
        this.service = service;
    }

    @GetMapping("/{symbol}")
    public List<BalanceSheetEntity> getBySymbol(@PathVariable("symbol") String symbol) {
        return service.getBySymbol(symbol);
    }

    @GetMapping("/{symbol}/{exchange}")
    public List<BalanceSheetEntity> getBySymbolAndExchange(@PathVariable("symbol") String symbol,
                                                           @PathVariable("exchange") String exchange) {
        return service.getBySymbolAndExchange(symbol, exchange);
    }
}
