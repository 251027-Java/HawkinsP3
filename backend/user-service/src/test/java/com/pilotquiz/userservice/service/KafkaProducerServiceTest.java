package com.pilotquiz.userservice.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    @Test
    @DisplayName("Should send user registered event")
    void sendUserRegisteredEvent_Success() {
        // Given
        Long userId = 1L;
        String email = "test@example.com";

        // When
        kafkaProducerService.sendUserRegisteredEvent(userId, email);

        // Then
        verify(kafkaTemplate).send(eq("user.registered"), eq(String.valueOf(userId)),
                org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Should send profile updated event")
    void sendProfileUpdatedEvent_Success() {
        // Given
        Long userId = 1L;

        // When
        kafkaProducerService.sendProfileUpdatedEvent(userId);

        // Then
        verify(kafkaTemplate).send(eq("user.profile.updated"), eq(String.valueOf(userId)),
                org.mockito.ArgumentMatchers.any());
    }
}
