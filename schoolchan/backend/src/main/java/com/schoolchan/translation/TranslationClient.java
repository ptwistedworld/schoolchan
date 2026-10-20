package com.schoolchan.translation;

import com.fasterxml.jackson.databind.JsonNode;
import com.schoolchan.config.SchoolChanProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@Component
public class TranslationClient {

    private final SchoolChanProperties properties;
    private final RestTemplate restTemplate;

    public TranslationClient(SchoolChanProperties properties, RestTemplateBuilder builder) {
        this.properties = properties;
        this.restTemplate = builder
                .setConnectTimeout(Duration.ofMillis(properties.getMl().getTimeoutMs()))
                .setReadTimeout(Duration.ofMillis(properties.getMl().getTimeoutMs()))
                .build();
    }

    public Optional<TranslationResult> translate(String text) {
        if (!properties.getMl().isEnabled() || text == null || text.isBlank()) {
            return Optional.empty();
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(Map.of("text", text), headers);
            String url = properties.getMl().getBaseUrl() + "/v1/translate";
            JsonNode body = restTemplate.postForObject(url, entity, JsonNode.class);
            if (body == null) {
                return Optional.empty();
            }
            return Optional.of(new TranslationResult(
                    body.path("translated_text").asText(text),
                    body.path("detected_language").asText("unknown"),
                    body.path("model_id").asText("unknown")));
        } catch (RestClientException ex) {
            return Optional.empty();
        }
    }

    public record TranslationResult(String translatedText, String detectedLanguage, String modelId) {}
}
