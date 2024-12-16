package com.stockfetcher.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "metadata")
public class Metadata {

	@Id
	private String symbol;
	private String companyName;
	private String sector;
	private long marketCap;
	private String currency;

	// Getters and Setters
}
