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
@Table(name = "insider_transactions")
public class InsiderTransactionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "meta_id", nullable = false)
	private MetaInfo metaInfo;

	@Column(name = "full_name")
	private String fullName;

	@Column(name = "position")
	private String position;

	@Column(name = "date_reported")
	private LocalDate dateReported;

	@Column(name = "is_direct")
	private Boolean isDirect;

	@Column(name = "shares")
	private Long shares;

	@Column(name = "value", precision = 20, scale = 2)
	private BigDecimal value;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;
}
