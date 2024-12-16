package com.stockfetcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class StockfetcherApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockfetcherApplication.class, args);
	}

}
