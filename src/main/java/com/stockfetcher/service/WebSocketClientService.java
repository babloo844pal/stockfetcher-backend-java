package com.stockfetcher.service;

import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import jakarta.annotation.PostConstruct;

@Service
public class WebSocketClientService extends AbstractWebSocketHandler {

    private static final String WEBSOCKET_URL = "wss://ws-api.iexcloud.io/stable/";
    private static final String TOKEN = "YOUR_IEX_CLOUD_API_KEY"; // Replace with your IEX Cloud API key
    private static final String SUBSCRIBE_MESSAGE_TEMPLATE = "{\"token\": \"%s\", \"event\": \"subscribe\", \"symbols\": \"%s\"}";

    private final RealTimeStockService realTimeStockService;

    public WebSocketClientService(RealTimeStockService realTimeStockService) {
        this.realTimeStockService = realTimeStockService;
    }

    @PostConstruct
    public void initialize() {
        Executors.newSingleThreadExecutor().execute(() -> {
            StandardWebSocketClient client = new StandardWebSocketClient();
            client.doHandshake(this, WEBSOCKET_URL);
        });
    }

    @Override
    public void afterConnectionEstablished(org.springframework.web.socket.WebSocketSession session) throws Exception {
        // Subscribe to stock symbols (e.g., AAPL, TSLA)
        String subscribeMessage = String.format(SUBSCRIBE_MESSAGE_TEMPLATE, TOKEN, "AAPL,TSLA,GOOG");
        session.sendMessage(new TextMessage(subscribeMessage));
        System.out.println("WebSocket connection established and subscription sent.");
    }

    @Override
    public void handleTextMessage(org.springframework.web.socket.WebSocketSession session, TextMessage message) {
        System.out.println("Received message: " + message.getPayload());

        // Process the received message and pass to the real-time service
        realTimeStockService.processRealTimeStockData(message.getPayload());
    }
}
