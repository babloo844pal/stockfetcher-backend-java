package com.stockfetcher.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;


@Data
public class WatchlistResponseDto {

	private Long id;

	@JsonProperty("userId") // Map JSON "userId" to this field
	private Long userId;

	private String name;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

}
