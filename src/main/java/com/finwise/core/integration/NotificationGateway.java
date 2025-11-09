package com.finwise.core.integration;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class NotificationGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationGateway.class);

    private final RestTemplate restTemplate;
    private final String notificationUrl;

    public NotificationGateway(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${finwise.notification.url:http://localhost:5025/graphql}") String notificationUrl) {
        this.restTemplate = restTemplateBuilder.setConnectTimeout(java.time.Duration.ofSeconds(5))
                .setReadTimeout(java.time.Duration.ofSeconds(5))
                .build();
        this.notificationUrl = notificationUrl;
    }

    public void notify(String title, String message, String userId) {
        try {
            String mutation = """
                    mutation SendNotification($input: CreateNotificationInput!) {
                      sendNotification(input: $input) {
                        id
                      }
                    }
                    """;

            Map<String, Object> payload = Map.of(
                    "query", mutation,
                    "variables", Map.of(
                            "input", Map.of(
                                    "userId", userId,
                                    "title", title,
                                    "message", message)));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            restTemplate.postForEntity(notificationUrl, request, String.class);
        } catch (Exception ex) {
            LOGGER.warn("No se pudo enviar notificación al servicio de notificaciones: {}", ex.getMessage());
        }
    }
}


