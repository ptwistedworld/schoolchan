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
<<<<<<< HEAD
=======
        // The classifier is English-only. Scoring raw Tagalog or Cebuano text
        // produces false positives, so:
        //   - if there is a translation, score the translation only;
        //   - if the text is English, score it;
        //   - otherwise skip, because we have nothing the model understands.
        String language = post == null ? null : post.getDetectedLanguage();
        String translation = post == null ? null : post.getTranslatedText();
        boolean hasTranslation = translation != null
                && !translation.isBlank()
                && (post.getOriginalText() == null || !translation.equals(post.getOriginalText()));
        String textToScore = combinedText;
        if (hasTranslation) {
            textToScore = translation;
        } else if (language != null && !language.isBlank() && !"en".equalsIgnoreCase(language)) {
            long ms = (System.nanoTime() - start) / 1_000_000;
            return new StageResult(name(), 0, ms).withReason("ml-skipped: needs translation (" + language + ")");
        }
>>>>>>> d5851d3 (Initial commit)
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity =
<<<<<<< HEAD
                    new HttpEntity<>(Map.of("text", combinedText), headers);
=======
                    new HttpEntity<>(Map.of("text", textToScore), headers);
>>>>>>> d5851d3 (Initial commit)
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
