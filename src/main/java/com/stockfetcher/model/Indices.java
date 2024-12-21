package com.stockfetcher.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "indices")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Indices {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private Long id;
	private String symbol;
	private String name;
	private String country;
	private String currency;
	private String exchange;

	@JsonProperty("mic_code")
	@Column(name = "mic_code", nullable = false)
	private String micCode;
}
