package com.stockfetcher.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stockfetcher.model.IncomeStatementEntity;
import com.stockfetcher.service.IncomeStatementService;

@RestController
@RequestMapping("/fetchStock/twelvedata")
public class IncomeStatementController {

    private final IncomeStatementService incomeStatementService;

    public IncomeStatementController(IncomeStatementService incomeStatementService) {
        this.incomeStatementService = incomeStatementService;
    }

    @GetMapping("/income-statements")
    public List<IncomeStatementEntity> getIncomeStatements(@RequestParam String symbol, @RequestParam String apiKey) {
        return incomeStatementService.getIncomeStatements(symbol, apiKey);
    }

    @GetMapping("/income-statement/fiscal-date")
    public IncomeStatementEntity getIncomeStatement(@RequestParam String symbol, @RequestParam String fiscalDate, @RequestParam String apiKey) {
        return incomeStatementService.getIncomeStatement(symbol, fiscalDate, apiKey);
    }
}
