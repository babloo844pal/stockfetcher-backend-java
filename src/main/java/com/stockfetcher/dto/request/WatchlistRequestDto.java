package com.stockfetcher.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WatchlistRequestDto {
	
	@JsonProperty("userId")
	@NotNull
	private Long userId;

	@JsonProperty("name")
	@NotNull
	private String name;

	// Getters and Setters
}
