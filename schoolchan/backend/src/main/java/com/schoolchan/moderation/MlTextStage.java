package com.schoolchan.moderation;

import com.fasterxml.jackson.databind.JsonNode;
import com.schoolchan.board.Post;
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

@Component
public class MlTextStage implements ModerationStage {

    private final SchoolChanProperties properties;
    private final RestTemplate restTemplate;
    public MlTextStage(
            SchoolChanProperties properties, RestTemplateBuilder builder) {
        this.properties = properties;
        this.restTemplate = builder
                .setConnectTimeout(Duration.ofMillis(properties.getMl().getTimeoutMs()))
                .setReadTimeout(Duration.ofMillis(properties.getMl().getTimeoutMs()))
                .build();
    }

    @Override
    public String name() {
        return "ml-text";
    }

    @Override
    public StageResult evaluate(Post post, String combinedText) {
        long start = System.nanoTime();
        if (!properties.getMl().isEnabled() || properties.getModeration().isBaselineOnly()) {
            long ms = (System.nanoTime() - start) / 1_000_000;
            return new StageResult(name(), 0, ms);
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity =
                    new HttpEntity<>(Map.of("text", combinedText), headers);
            String url = properties.getMl().getBaseUrl() + "/v1/text/classify";
            JsonNode body = restTemplate.postForObject(url, entity, JsonNode.class);
            double max = 0;
            StageResult result = new StageResult(name(), 0, 0);
            if (body != null) {
                for (String field : new String[] {"toxicity", "hate", "harassment", "sexual", "threat", "self_harm"}) {
                    if (body.has(field)) {
                        double v = body.get(field).asDouble();
                        max = Math.max(max, v);
                        if (v >= 0.5) {
                            result.withReason(field + "=" + v);
                        }
                    }
                }
            }
            long ms = (System.nanoTime() - start) / 1_000_000;
            StageResult out = new StageResult(name(), max, ms);
            out.getReasons().addAll(result.getReasons());
            return out;
        } catch (RestClientException ex) {
            long ms = (System.nanoTime() - start) / 1_000_000;
            return new StageResult(name(), 0, ms, true).withReason("ml-unavailable");
        }
    }
}
