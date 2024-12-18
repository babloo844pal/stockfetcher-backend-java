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
@Table(name = "technical_indicators_data")
public class TechnicalIndicatorsData {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@JsonProperty("name")
    private String name;

	@JsonProperty("full_name")
    @Column(name = "full_name")
    private String fullName;

	@JsonProperty("type")
    private String type;

	@JsonProperty("overlay")
    private Boolean overlay;

	@JsonProperty("series_type")
    @Column(name = "series_type")
    private String seriesType;

	@JsonProperty("default_color")
    @Column(name = "default_color")
    private String defaultColor;

	@JsonProperty("display")
    private String display;

	@JsonProperty("description")
    private String description;

}
