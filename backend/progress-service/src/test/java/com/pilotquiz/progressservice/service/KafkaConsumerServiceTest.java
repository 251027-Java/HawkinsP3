package com.pilotquiz.progressservice.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerServiceTest {

    @Mock
    private ProgressService progressService;

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;

    @Test
    @DisplayName("Should handle user registered event without error")
    void handleUserRegistered_Success() {
        // Given
        Map<String, Object> event = new HashMap<>();
        event.put("userId", 1L);
        event.put("email", "test@example.com");

        // When/Then
        assertDoesNotThrow(() -> kafkaConsumerService.handleUserRegistered(event));
    }

    @Test
    @DisplayName("Should handle profile updated event without error")
    void handleProfileUpdated_Success() {
        // Given
        Map<String, Object> event = new HashMap<>();
        event.put("userId", 1L);

        // When/Then
        assertDoesNotThrow(() -> kafkaConsumerService.handleProfileUpdated(event));
    }
}
