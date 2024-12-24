/*
 * package com.stockfetcher.controller;
 * 
 * import java.util.List;
 * 
 * import org.springframework.web.bind.annotation.GetMapping; import
 * org.springframework.web.bind.annotation.PathVariable; import
 * org.springframework.web.bind.annotation.RequestMapping; import
 * org.springframework.web.bind.annotation.RestController;
 * 
 * import com.stockfetcher.model.InstitutionalHolderEntity; import
 * com.stockfetcher.service.InstitutionalHolderService;
 * 
 * @RestController
 * 
 * @RequestMapping("/institutional-holders") public class
 * InstitutionalHolderController {
 * 
 * private final InstitutionalHolderService holderService;
 * 
 * public InstitutionalHolderController(InstitutionalHolderService
 * holderService) { this.holderService = holderService; }
 * 
 * @GetMapping("/{symbol}") public List<InstitutionalHolderEntity>
 * getBySymbol(@PathVariable("symbol") String symbol) { return
 * holderService.getBySymbol(symbol); } }
 */