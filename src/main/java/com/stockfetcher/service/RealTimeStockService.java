/*
 * package com.stockfetcher.service;
 * 
 * import java.math.BigDecimal; import java.time.LocalDateTime;
 * 
 * import org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.kafka.core.KafkaTemplate; import
 * org.springframework.stereotype.Service; import
 * org.springframework.web.reactive.function.client.WebClient;
 * 
 * import com.fasterxml.jackson.databind.JsonNode; import
 * com.fasterxml.jackson.databind.ObjectMapper; import
 * com.stockfetcher.model.StockData; import
 * com.stockfetcher.processor.RealTimeDataProcessor; import
 * com.stockfetcher.repository.StockDataRepository;
 * 
 * import lombok.AllArgsConstructor;
 * 
 * @Service
 * 
 * @AllArgsConstructor public class RealTimeStockService {
 * 
 * private final WebClient webClient; private final RealTimeDataProcessor
 * realTimeDataProcessor;
 * 
 * private final KafkaTemplate<String, StockData> kafkaTemplate; private final
 * ObjectMapper objectMapper;
 * 
 * @Autowired private AlphaVantageService alphaVantageService;
 * 
 * @Autowired private StockDataRepository stockDataRepository;
 * 
 * @Autowired TwelveDataService TwelveDataService;
 * 
 * 
 * 
 * public void fetchRealTimeData(String symbol) { StockData
 * stockData=alphaVantageService.fetchRealTimeData(symbol); if(stockData !=null)
 * { stockDataRepository.save(stockData); } }
 * 
 * 
 * 
 * public void fetchRealTimeData(String symbol) { StockData
 * stockData=TwelveDataService.fetchRealTimeData(symbol); if(stockData !=null) {
 * stockDataRepository.save(stockData); } }
 * 
 * public void processRealTimeStockData(String messagePayload) { try { JsonNode
 * rootNode = objectMapper.readTree(messagePayload);
 * 
 * for (JsonNode dataNode : rootNode) { StockData stockData = new StockData();
 * stockData.setSymbol(dataNode.get("symbol").asText()); stockData.setPrice(new
 * BigDecimal(dataNode.get("price").asText()));
 * stockData.setVolume(dataNode.get("volume").asLong());
 * stockData.setTimestamp(LocalDateTime.parse(dataNode.get("lastSaleTime").
 * asText() ));
 * 
 * // Push processed stock data to Kafka
 * kafkaTemplate.send("realtime-stock-data", stockData.getSymbol(), stockData);
 * 
 * System.out.println("Processed and sent stock data: " + stockData); } } catch
 * (Exception e) { System.err.println("Error processing real-time stock data: "
 * + e.getMessage()); } } }
 */