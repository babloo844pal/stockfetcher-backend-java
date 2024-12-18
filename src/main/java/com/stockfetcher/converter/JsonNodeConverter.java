package com.stockfetcher.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class JsonNodeConverter implements AttributeConverter<JsonNode, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(JsonNode jsonNode) {
        try {
            return jsonNode != null ? objectMapper.writeValueAsString(jsonNode) : null;
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting JSON to String", e);
        }
    }

    @Override
    public JsonNode convertToEntityAttribute(String json) {
        try {
            return json != null ? objectMapper.readTree(json) : null;
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting String to JSON", e);
        }
    }
}
