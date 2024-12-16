package com.stockfetcher.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;

@Configuration
public class ElasticsearchConfig {

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        // Create the low-level REST client
        RestClient restClient = RestClient.builder(
                new HttpHost("localhost", 9200, "http") // Change to your Elasticsearch host and port
        ).build();

        // Create the transport with Jackson mapper
        RestClientTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper()
        );

        // Create the API client
        return new ElasticsearchClient(transport);
    }
}
