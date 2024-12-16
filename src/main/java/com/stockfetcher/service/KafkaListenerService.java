package com.stockfetcher.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaListenerService {

    @KafkaListener(topics = "realtime-stock-data", groupId = "stockfetcher-group")
    public void consume(String message) {
        System.out.println("Consumed message: " + message);
        // Process the message as needed
    }
}
