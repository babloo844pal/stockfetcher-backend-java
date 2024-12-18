package com.stockfetcher.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockfetch.cache.RedisService;
import com.stockfetcher.enums.Interval_Slice;
import com.stockfetcher.model.StockMeta;
import com.stockfetcher.model.StockPrice;
import com.stockfetcher.repository.StockMetaRepository;
import com.stockfetcher.repository.StockPriceRepository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;

@Service
public class StockService {

	private static final Logger logger = LoggerFactory.getLogger(StockService.class);

	private final StockMetaRepository stockMetaRepository;
	private final StockPriceRepository stockPriceRepository;
	private final RedisTemplate<String, Object> redisTemplate;
	private final ElasticsearchClient elasticsearchClient;
	
	private final ObjectMapper objectMapper;

	private final List<String> intervelInTime = Arrays.asList("1min", "5min", "15min", "30min", "45min", "1h", "2h",
			"4h", "8h");

	@Autowired
	private RedisService redisService;
	
	public StockService(StockMetaRepository stockMetaRepository, StockPriceRepository stockPriceRepository,
			RedisTemplate<String, Object> redisTemplate, ElasticsearchClient elasticsearchClient,
			ObjectMapper objectMapper) {
		this.stockMetaRepository = stockMetaRepository;
		this.stockPriceRepository = stockPriceRepository;
		this.redisTemplate = redisTemplate;
		this.elasticsearchClient = elasticsearchClient;
		this.objectMapper = objectMapper;
	}

	/**
	 * Processes the payload, stores metadata and stock price data, and ensures
	 * deduplication.
	 *
	 * @param payload JSON payload received from the API
	 */
	public void processStockPayload(String payload) {
		try {
			JsonNode rootNode = objectMapper.readTree(payload);

			// Parse and save stock metadata
			JsonNode metaNode = rootNode.get("meta");
			StockMeta stockMeta = parseStockMeta(metaNode);
			stockMetaRepository.save(stockMeta);

			// Parse and save stock price data
			JsonNode valuesNode = rootNode.get("values");
			Interval_Slice interval = Interval_Slice.fromValue(stockMeta.getIntervalSlice());
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(determineDateFormat(interval));

			for (JsonNode valueNode : valuesNode) {
				LocalDateTime datetime = dateTimeConverter(stockMeta.getIntervalSlice(),
						valueNode.get("datetime").asText(), formatter);

				// Check for duplicates in the database
				boolean exists = stockPriceRepository.existsBySymbolAndIntervalSliceAndDatetime(stockMeta.getSymbol(),
						stockMeta.getIntervalSlice(), datetime);

				if (!exists) {
					StockPrice stockPrice = parseStockPrice(valueNode, stockMeta.getSymbol(),
							stockMeta.getIntervalSlice(), datetime);

					// Save to database
					stockPriceRepository.save(stockPrice);

					// Cache in Redis
					redisService.saveStockPrice(stockPrice);

					// Index to Elasticsearch
					indexStockPrice(stockPrice);
				}
			}
		} catch (Exception e) {
			logger.error("Error processing stock payload: {}", e.getMessage(), e);
		}
	}

	/**
	 * Parses and creates a StockMeta object from the JSON node.
	 *
	 * @param metaNode JSON node for metadata
	 * @return StockMeta object
	 */
	private StockMeta parseStockMeta(JsonNode metaNode) {
		StockMeta stockMeta = new StockMeta();
		stockMeta.setSymbol(metaNode.get("symbol").asText());
		stockMeta.setIntervalSlice(metaNode.get("interval").asText());
		stockMeta.setCurrency(metaNode.get("currency").asText());
		stockMeta.setExchange_timezone(metaNode.get("exchange_timezone").asText());
		stockMeta.setExchange(metaNode.get("exchange").asText());
		stockMeta.setMic_code(metaNode.get("mic_code").asText());
		stockMeta.setType(metaNode.get("type").asText());
		return stockMeta;
	}

	/**
	 * Parses and creates a StockPrice object from the JSON node.
	 *
	 * @param valueNode JSON node for stock price
	 * @param symbol    Stock symbol
	 * @param interval  Interval
	 * @param datetime  Datetime
	 * @return StockPrice object
	 */
	private StockPrice parseStockPrice(JsonNode valueNode, String symbol, String interval, LocalDateTime datetime) {
		StockPrice stockPrice = new StockPrice();
		stockPrice.setSymbol(symbol);
		stockPrice.setIntervalSlice(interval);
		stockPrice.setDatetime(datetime);
		stockPrice.setOpen(new BigDecimal(valueNode.get("open").asText()));
		stockPrice.setHigh(new BigDecimal(valueNode.get("high").asText()));
		stockPrice.setLow(new BigDecimal(valueNode.get("low").asText()));
		stockPrice.setClose(new BigDecimal(valueNode.get("close").asText()));
		stockPrice.setVolume(valueNode.get("volume").asLong());
		return stockPrice;
	}

	/**
	 * Determines the date format based on the interval.
	 *
	 * @param interval Interval
	 * @return Date format string
	 */
	private String determineDateFormat(Interval_Slice interval) {
		switch (interval) {
		case ONE_MINUTE:
		case FIVE_MINUTES:
		case FIFTEEN_MINUTES:
		case THIRTY_MINUTES:
		case FORTY_FIVE_MINUTES:
		case ONE_HOUR:
		case TWO_HOURS:
		case FOUR_HOURS:
		case EIGHT_HOURS:
			return "yyyy-MM-dd HH:mm:ss";
		case ONE_DAY:
		case ONE_WEEK:
		case ONE_MONTH:
			return "yyyy-MM-dd";
		default:
			throw new IllegalArgumentException("Unsupported interval: " + interval);
		}
	}

	/**
	 * Index stock price data into Elasticsearch.
	 *
	 * @param stockPrice StockPrice object
	 */
	public void indexStockPrice(StockPrice stockPrice) {
		try {
			elasticsearchClient.index(i -> i.index("stock_prices")
					.id(stockPrice.getSymbol() + "_" + stockPrice.getIntervalSlice() + "_" + stockPrice.getDatetime())
					.document(stockPrice));
			logger.info("Indexed stock price data: {}", stockPrice);
		} catch (Exception e) {
			logger.error("Failed to index stock price data: {}", e.getMessage(), e);
		}
	}

	/**
	 * Generates a Redis cache key for a stock price record.
	 *
	 * @param stockPrice StockPrice object
	 * @return Cache key string
	 */
	private String getCacheKey(StockPrice stockPrice) {
		return stockPrice.getSymbol() + "_" + stockPrice.getIntervalSlice() + "_" + stockPrice.getDatetime().toString();
	}

	public LocalDateTime dateTimeConverter(String interval_slice, String localDateTimeText,
			DateTimeFormatter formatter) {
		LocalDateTime result;
		if (!intervelInTime.contains(interval_slice)) {
			LocalDate localData = LocalDate.parse(localDateTimeText, formatter);
			result = LocalDateTime.of(localData, LocalTime.of(0, 0));
		} else {
			result = LocalDateTime.parse(localDateTimeText, formatter);
		}

		return result;

	}

}
