package com.stockfetcher.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stockfetcher.model.BalanceSheet;
import com.stockfetcher.service.BalanceSheetService;

@RestController
@RequestMapping("/api/balance-sheet")
public class BalanceSheetController {

	@Autowired
	private BalanceSheetService balanceSheetService;

	@GetMapping
	public List<BalanceSheet> getBalanceSheet(@RequestParam String symbol, @RequestParam String exchange,
			@RequestParam(required = false) String micCode) {
		return balanceSheetService.getBalanceSheet(symbol, exchange, micCode);
	}
}
