// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.userservice.service;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        // Set up test secret key (min 256 bits for HS256)
        String secretKey = "this-is-a-test-secret-key-that-is-long-enough-for-hs256-algorithm";
        ReflectionTestUtils.setField(jwtService, "secret", secretKey);
        ReflectionTestUtils.setField(jwtService, "expiration", 86400000L); // 24 hours
    }

    @Test
    @DisplayName("Should generate valid JWT token")
    void generateToken_Success() {
        // When
        String token = jwtService.generateToken(1L, "test@example.com", "ROLE_USER");

        // Then
        assertThat(token).isNotNull();
        assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts
    }

    @Test
    @DisplayName("Should extract user ID from token")
    void extractUserId_Success() {
        // Given
        String token = jwtService.generateToken(1L, "test@example.com", "ROLE_USER");

        // When
        Long userId = jwtService.extractUserId(token);

        // Then
        assertThat(userId).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should validate token and extract claims")
    void validateToken_Success() {
        // Given
        String token = jwtService.generateToken(1L, "test@example.com", "ROLE_USER");

        // When
        Claims claims = jwtService.validateToken(token);

        // Then
        assertThat(claims).isNotNull();
        assertThat(claims.getSubject()).isEqualTo("1");
        assertThat(claims.get("email")).isEqualTo("test@example.com");
        assertThat(claims.get("role")).isEqualTo("ROLE_USER");
    }

    @Test
    @DisplayName("Should return false for expired check on valid token")
    void isTokenExpired_ValidToken_ReturnsFalse() {
        // Given
        String token = jwtService.generateToken(1L, "test@example.com", "ROLE_USER");

        // When
        boolean isExpired = jwtService.isTokenExpired(token);

        // Then
        assertThat(isExpired).isFalse();
    }

    @Test
    @DisplayName("Should return true for invalid token expiry check")
    void isTokenExpired_InvalidToken_ReturnsTrue() {
        // When
        boolean isExpired = jwtService.isTokenExpired("invalid.token.here");

        // Then
        assertThat(isExpired).isTrue();
    }
}
