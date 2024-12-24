package com.stockfetcher.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stockfetcher.model.IncomeStatement;
import com.stockfetcher.service.IncomeStatementService;

@RestController
@RequestMapping("/income-statement")
public class IncomeStatementController {

	@Autowired
	private IncomeStatementService incomeStatementService;

	@GetMapping
	public List<IncomeStatement> getIncomeStatements(@RequestParam String symbol, @RequestParam String exchange) {
		return incomeStatementService.getIncomeStatements(symbol, exchange);
	}
}
