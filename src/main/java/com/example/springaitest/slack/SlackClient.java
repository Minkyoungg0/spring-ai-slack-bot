// src/main/java/com/example/springaitest/slack/SlackClient.java
package com.example.springaitest.slack;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SlackClient {

    private final RestTemplate rest = new RestTemplate();

    @Value("${app.slack.webhook-url:}")
    private String webhookUrl;

    @Value("${app.slack.enabled:true}")
    private boolean enabled;

    public void sendText(String text) {
        if (!enabled) return;
        if (webhookUrl == null || webhookUrl.isBlank()) {
            System.err.println("Slack disabled: webhook URL is empty");
            return;
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, String> body = Map.of("text", text);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
            rest.postForEntity(webhookUrl, entity, String.class);
        } catch (RestClientException e) {
            System.err.println("Slack send failed: " + e.getMessage());
        }
    }
}
