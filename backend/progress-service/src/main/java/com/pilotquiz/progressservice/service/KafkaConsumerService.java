// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.progressservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Kafka consumer for user and quiz events.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    /**
     * Handle user registration events.
     * Initialize progress tracking for new users.
     */
    @KafkaListener(topics = "user.registered", groupId = "progress-service")
    public void handleUserRegistered(Map<String, Object> event) {
        Long userId = ((Number) event.get("userId")).longValue();
        String email = (String) event.get("email");

        log.info("Received user.registered event for userId: {}, email: {}", userId, email);

        // Could initialize default progress records or welcome achievements here
    }

    /**
     * Handle profile update events.
     */
    @KafkaListener(topics = "user.profile.updated", groupId = "progress-service")
    public void handleProfileUpdated(Map<String, Object> event) {
        Long userId = ((Number) event.get("userId")).longValue();

        log.info("Received user.profile.updated event for userId: {}", userId);

        // Could sync category preferences here
    }
}
