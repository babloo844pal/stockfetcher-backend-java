package com.stockfetcher.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "institutional_holders")
public class InstitutionalHolderEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "meta_id", nullable = false)
	private MetaInfo metaInfo;

	@Column(name = "entity_name")
	private String entityName;

	@Column(name = "date_reported")
	private LocalDate dateReported;

	@Column(name = "shares")
	private Long shares;

	@Column(name = "value", precision = 20, scale = 2)
	private BigDecimal value;

	@Column(name = "percent_held", precision = 10, scale = 8)
	private BigDecimal percentHeld;
}
