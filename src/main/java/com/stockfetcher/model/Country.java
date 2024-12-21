package com.stockfetcher.model;

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
@Table(name = "countries")
public class Country {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String iso2; // 2-letter ISO code
	private String iso3; // 3-letter ISO code

	@JsonProperty("numeric")
	@Column(name = "numeric_code", nullable = false)
	private String numeric_code; // Numeric code
	private String name; // Country name
	private String officialName; // Official country name
	private String capital; // Capital city
	private String currency; // Currency code
}
