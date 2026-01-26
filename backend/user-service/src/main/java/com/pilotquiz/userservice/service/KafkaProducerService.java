// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service for publishing events to Kafka.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private static final String USER_REGISTERED_TOPIC = "user.registered";
    private static final String PROFILE_UPDATED_TOPIC = "user.profile.updated";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Publish user registered event.
     */
    public void sendUserRegisteredEvent(Long userId, String email) {
        log.info("Publishing user.registered event for userId: {}", userId);

        Map<String, Object> event = Map.of(
                "userId", userId,
                "email", email,
                "timestamp", System.currentTimeMillis());

        kafkaTemplate.send(USER_REGISTERED_TOPIC, userId.toString(), event);
    }

    /**
     * Publish profile updated event.
     */
    public void sendProfileUpdatedEvent(Long userId) {
        log.info("Publishing user.profile.updated event for userId: {}", userId);

        Map<String, Object> event = Map.of(
                "userId", userId,
                "timestamp", System.currentTimeMillis());

        kafkaTemplate.send(PROFILE_UPDATED_TOPIC, userId.toString(), event);
    }
}
