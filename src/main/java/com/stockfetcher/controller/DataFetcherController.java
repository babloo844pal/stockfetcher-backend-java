/*
 * package com.stockfetcher.controller;
 * 
 * import com.stockfetcher.service.DataFetcherService; import
 * org.springframework.web.bind.annotation.GetMapping; import
 * org.springframework.web.bind.annotation.RequestParam; import
 * org.springframework.web.bind.annotation.RestController;
 * 
 * @RestController public class DataFetcherController {
 * 
 * private final DataFetcherService dataFetcherService;
 * 
 * public DataFetcherController(DataFetcherService dataFetcherService) {
 * this.dataFetcherService = dataFetcherService; }
 * 
 * @GetMapping("/fetch-historical") public String
 * fetchHistoricalData(@RequestParam String symbol) {
 * dataFetcherService.fetchHistoricalData(symbol); return
 * "Historical data fetch initiated for symbol: " + symbol; }
 * 
 * @GetMapping("/stream-realtime") public String
 * streamRealTimeData(@RequestParam String symbol) {
 * dataFetcherService.streamRealTimeData(symbol); return
 * "Real-time data streaming initiated for symbol: " + symbol; } }
 */