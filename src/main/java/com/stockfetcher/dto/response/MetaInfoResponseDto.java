package com.stockfetcher.dto.response;

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
}
