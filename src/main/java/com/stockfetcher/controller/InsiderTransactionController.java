package com.stockfetcher.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stockfetcher.model.InsiderTransactionEntity;
import com.stockfetcher.service.InsiderTransactionService;

@RestController
@RequestMapping("/insider-transactions")
public class InsiderTransactionController {

    private final InsiderTransactionService service;

    public InsiderTransactionController(InsiderTransactionService service) {
        this.service = service;
    }

    @GetMapping("/{symbol}")
    public List<InsiderTransactionEntity> getBySymbol(@PathVariable("symbol") String symbol) {
        return service.getBySymbol(symbol);
    }
}
