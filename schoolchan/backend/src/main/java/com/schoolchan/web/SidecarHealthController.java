package com.schoolchan.web;

import com.schoolchan.config.SchoolChanProperties;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Small JSON health endpoint for the moderation sidecar. Staff-only, used by
 * the dashboard and by the demo script to prove the sidecar is up.
 */
@RestController
public class SidecarHealthController {

    private final SchoolChanProperties properties;
    private final RestTemplate restTemplate;

    public SidecarHealthController(SchoolChanProperties properties) {
        this.properties = properties;
        var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(1500);
        factory.setReadTimeout(2000);
        this.restTemplate = new RestTemplate(factory);
    }

    @GetMapping("/staff/api/sidecar-health")
    public Map<String, Object> health() {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("baseUrl", properties.getMl().getBaseUrl());
        out.put("enabled", properties.getMl().isEnabled());
        try {
            JsonNode body = restTemplate.getForObject(
                    properties.getMl().getBaseUrl() + "/health", JsonNode.class);
            out.put("reachable", true);
            out.put("status", body == null ? "unknown" : body.path("status").asText("unknown"));
            out.put("mockMode", body != null && body.path("mock_mode").asBoolean(false));
            out.put("version", body == null ? null : body.path("version").asText(null));
            if (body != null && body.has("warnings")) {
                out.put("warnings", body.get("warnings"));
            }
        } catch (Exception e) {
            out.put("reachable", false);
            out.put("error", e.getClass().getSimpleName());
        }
        return out;
    }
}
