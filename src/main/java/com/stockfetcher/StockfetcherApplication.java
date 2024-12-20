package com.stockfetcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StockfetcherApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockfetcherApplication.class, args);
	}

}
