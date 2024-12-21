package com.stockfetcher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stockfetcher.model.Country;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

	Country findByIso3IgnoreCase(String iso3);
	//List<Country> findByNameContainingIgnoreCase(String name);

}
