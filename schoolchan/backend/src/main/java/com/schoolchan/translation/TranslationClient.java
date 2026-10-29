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

<<<<<<< HEAD
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
=======
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
>>>>>>> d5851d3 (Initial commit)

@Component
public class TranslationClient {

    private final SchoolChanProperties properties;
    private final RestTemplate restTemplate;
<<<<<<< HEAD
=======
    private final ConcurrentHashMap<String, TranslationResult> cache = new ConcurrentHashMap<>();
>>>>>>> d5851d3 (Initial commit)

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
<<<<<<< HEAD
=======
        String key = sha256(text.trim());
        TranslationResult cached = cache.get(key);
        if (cached != null) {
            return Optional.of(cached);
        }
>>>>>>> d5851d3 (Initial commit)
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(Map.of("text", text), headers);
            String url = properties.getMl().getBaseUrl() + "/v1/translate";
            JsonNode body = restTemplate.postForObject(url, entity, JsonNode.class);
            if (body == null) {
                return Optional.empty();
            }
<<<<<<< HEAD
            return Optional.of(new TranslationResult(
                    body.path("translated_text").asText(text),
                    body.path("detected_language").asText("unknown"),
                    body.path("model_id").asText("unknown")));
=======
            TranslationResult result = new TranslationResult(
                    body.path("translated_text").asText(text),
                    body.path("detected_language").asText("unknown"),
                    body.path("model_id").asText("unknown"));
            cache.put(key, result);
            return Optional.of(result);
>>>>>>> d5851d3 (Initial commit)
        } catch (RestClientException ex) {
            return Optional.empty();
        }
    }

    public record TranslationResult(String translatedText, String detectedLanguage, String modelId) {}
<<<<<<< HEAD
=======

    private static String sha256(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return text;
        }
    }
>>>>>>> d5851d3 (Initial commit)
}
