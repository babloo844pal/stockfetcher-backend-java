package com.stockfetcher.dto.response;

import com.stockfetcher.model.MetaInfo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MetaInfoResponseDto {
	private Long id;
	private String symbol;
	private String name;
	private String intervalTime;
	private String currency;
	private String exchangeTimezone;
	private String exchange;
	private String micCode;
	private String type;
	private String period;

	
	/**
     * Constructor to convert MetaInfo entity into MetaInfoResponseDto.
     */
    public MetaInfoResponseDto(Long id, String symbol, String name, String currency, String exchangeTimezone,
                               String exchange, String micCode, String type, String period) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.currency = currency;
        this.exchangeTimezone = exchangeTimezone;
        this.exchange = exchange;
        this.micCode = micCode;
        this.type = type;
        this.period = period;
    }
	
	/**
	 * Static method to map MetaInfo entity to MetaInfoResponseDto.
	 */
	public static MetaInfoResponseDto fromEntity(MetaInfo metaInfo) {
		return new MetaInfoResponseDto(metaInfo.getId(), metaInfo.getSymbol(), metaInfo.getName(),
				metaInfo.getCurrency(), metaInfo.getExchangeTimezone(), metaInfo.getExchange(), metaInfo.getMicCode(),
				metaInfo.getType(), metaInfo.getPeriod());
	}
}
