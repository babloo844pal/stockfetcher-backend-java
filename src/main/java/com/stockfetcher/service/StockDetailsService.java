package com.stockfetcher.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockfetcher.exception.ApiException;
import com.stockfetcher.model.FiftyTwoWeek;
import com.stockfetcher.model.StockDetails;
import com.stockfetcher.repository.StockDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class StockDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(StockDetailsService.class);

    private final StockDetailsRepository stockDetailsRepository;
    private final ObjectMapper objectMapper;

    public StockDetailsService(StockDetailsRepository stockDetailsRepository, ObjectMapper objectMapper) {
        this.stockDetailsRepository = stockDetailsRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Maps JSON data to StockDetails entity and saves it to the database.
     *
     * @param json JSON data from an external API
     */
    public void saveStockDetailsFromJson(String json) {
        try {
            JsonNode rootNode = objectMapper.readTree(json);

            StockDetails stockDetails = new StockDetails();
            stockDetails.setSymbol(rootNode.get("symbol").asText());
            stockDetails.setName(rootNode.get("name").asText());
            stockDetails.setExchange(rootNode.get("exchange").asText());
            stockDetails.setMic_code(rootNode.get("mic_code").asText());
            stockDetails.setCurrency(rootNode.get("currency").asText());
            stockDetails.setDatetime(LocalDate.parse(rootNode.get("datetime").asText()));
            stockDetails.setTimestamp(rootNode.get("timestamp").asLong());
            stockDetails.setOpen(new BigDecimal(rootNode.get("open").asText()));
            stockDetails.setHigh(new BigDecimal(rootNode.get("high").asText()));
            stockDetails.setLow(new BigDecimal(rootNode.get("low").asText()));
            stockDetails.setClose(new BigDecimal(rootNode.get("close").asText()));
            stockDetails.setVolume(Long.parseLong(rootNode.get("volume").asText()));
            stockDetails.setPrevious_close(new BigDecimal(rootNode.get("previous_close").asText()));
            stockDetails.setChange(new BigDecimal(rootNode.get("change").asText()));
            stockDetails.setPercent_change(new BigDecimal(rootNode.get("percent_change").asText()));
            stockDetails.setAverage_volume(Long.parseLong(rootNode.get("average_volume").asText()));
            stockDetails.setIs_market_open(rootNode.get("is_market_open").asBoolean());

            // Map the 52-week range details
            JsonNode fiftyTwoWeekNode = rootNode.get("fifty_two_week");
            FiftyTwoWeek fiftyTwoWeek = new FiftyTwoWeek();
            fiftyTwoWeek.setFifty_two_week_low(new BigDecimal(fiftyTwoWeekNode.get("low").asText()));
            fiftyTwoWeek.setFifty_two_week_high(new BigDecimal(fiftyTwoWeekNode.get("high").asText()));
            fiftyTwoWeek.setLow_change(new BigDecimal(fiftyTwoWeekNode.get("low_change").asText()));
            fiftyTwoWeek.setHigh_change(new BigDecimal(fiftyTwoWeekNode.get("high_change").asText()));
            fiftyTwoWeek.setLow_change_percent(new BigDecimal(fiftyTwoWeekNode.get("low_change_percent").asText()));
            fiftyTwoWeek.setHigh_change_percent(new BigDecimal(fiftyTwoWeekNode.get("high_change_percent").asText()));
            fiftyTwoWeek.setRange(fiftyTwoWeekNode.get("range").asText());

            stockDetails.setFiftyTwoWeek(fiftyTwoWeek);

            // Save the mapped entity to the database
            stockDetailsRepository.save(stockDetails);

            logger.info("Stock details for symbol '{}' saved successfully.", stockDetails.getSymbol());

        } catch (Exception e) {
            logger.error("Error occurred while mapping JSON to StockDetails: {}", e.getMessage(), e);
            throw new ApiException("Failed to process stock details JSON.", e);
        }
    }
}
